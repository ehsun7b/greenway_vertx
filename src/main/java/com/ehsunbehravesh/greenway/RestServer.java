package com.ehsunbehravesh.greenway;

import com.ehsunbehravesh.greenway.constant.Constants;
import com.ehsunbehravesh.greenway.telegram.model.Update;
import com.ehsunbehravesh.greenway.resource.FileResource;
import com.ehsunbehravesh.greenway.telegram.model.vertx.ChatState;
import com.ehsunbehravesh.greenway.telegram.model.vertx.DownloadVideoRequest;
import com.ehsunbehravesh.greenway.telegram.model.vertx.LoadVideoRequest;
import com.ehsunbehravesh.greenway.telegram.model.vertx.SendTelegramTextRequest;
import com.ehsunbehravesh.greenway.telegram.model.vertx.SendVideoRequest;
import com.ehsunbehravesh.greenway.telegram.utils.Utils;
import com.ehsunbehravesh.youtube.model.VideoProfile;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Ehsun Behravesh <post@ehsunbehravesh.com>
 */
public class RestServer extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(RestServer.class);
    private static final String HTTP_PORT_VARIABLE = "GREENWAY_HTTP_PORT";
    private static final String HTTPS_PORT_VARIABLE = "GREENWAY_HTTPS_PORT";
    private static final String KEY_STORE_VARIABLE = "GREENWAY_KEY_STORE";

    private Map<String, JsonObject> products = new HashMap<>();

    @Override
    public void start() {
        vertx.deployVerticle(new SendTelegramMessageVerticel());
        //vertx.deployVerticle(new YouTubeGetInfoVerticle());
        //vertx.deployVerticle(new YouTubeDownloadVideoVerticle());
        vertx.deployVerticle(new AccessLogVerticle());
        vertx.deployVerticle(new TelegramChatStateVerticle());
        vertx.deployVerticle(new PostCreateVerticle());

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create()); 
        router.route("/").handler(this::handleRoot);
        router.route("/196469941:AAH3ZYKro3NyJadh3N8IBhWsI6SAlfvh75I").handler(this::handleHook);

        int httpPort = getHttpPort();
        log.info("HTTP is enabled on port ".concat(httpPort + ""));
        vertx.createHttpServer().requestHandler(router::accept).listen(httpPort);

        String keyStore = System.getenv(KEY_STORE_VARIABLE);

        if (keyStore != null && new File(keyStore).exists()) {
            log.info("SSL keystore found: ".concat(keyStore));
            int httpsPort = getHttpsPort();
            log.info("HTTPS is enabled on port ".concat(httpsPort + ""));
            vertx.createHttpServer(
                    new HttpServerOptions().setSsl(true)
                    .setKeyStoreOptions(new JksOptions().setPath(keyStore).setPassword(getKeyStorePassword()))
            ).requestHandler(router::accept).listen(httpsPort);
        } else {
            log.info("SSL keystore NOT found! ".concat(KEY_STORE_VARIABLE));
        }

        vertx.eventBus().consumer(Constants.ADDR_LOAD_TELEGRAM_CHAT_STATE_RESULT, this::handleLoadStateResultMessage);
        vertx.eventBus().consumer(Constants.ADDR_YOUTUBE_VIDEO_LOAD_RESULT, this::handleLoadVideoResultMessage);
    }

    private void handleLoadVideoResultMessage(Message<Object> message) {
        log.info("Load video result received.");

        try {
            String json = message.body().toString();
            log.debug("Load video result json: " + json);

            Gson gson = new Gson();
            LoadVideoRequest result = gson.fromJson(json, LoadVideoRequest.class);

            if (result.getVideoProfile().getFilename() == null) {

                log.info("Sending video download request for chat id: " + result.getChatId() + " url: \n" + result.getVideoProfile().getUrl());

                try {
                    DownloadVideoRequest downloadVideoRequest = new DownloadVideoRequest(result.getChatId(), result.getVideoProfile());
                    String jsonToBeSent = gson.toJson(downloadVideoRequest);

                    vertx.eventBus().send(Constants.ADDR_YOUTUBE_DOWNLOAD_VIDEO, jsonToBeSent);
                } catch (JsonSyntaxException ex) {
                    log.error("Error in parsing video profile json in state", ex);
                } catch (Exception ex) {
                    log.error("Error in sending message into event bus", ex);
                }
            } else {
                String vidDir = System.getenv(Constants.VAR_VIDEO_DIR);
                File videoFile = new File(vidDir.concat(result.getVideoProfile().getFilename()));

                if (videoFile.exists() && videoFile.isFile()) {
                    log.info("The video file found. Sending telegram video request. video file: " + result.getVideoProfile().getFilename());

                    try {
                        SendVideoRequest sendVideoRequest = new SendVideoRequest(result.getVideoProfile(), result.getChatId());
                        
                        String sendVideoJson = gson.toJson(sendVideoRequest);
                        vertx.eventBus().send(Constants.ADDR_SEND_VIDEO_AS_TELEGRAM_MESSAGE, sendVideoJson);
                    } catch (JsonSyntaxException ex) {
                        log.error("Error in parsing video profile json in state", ex);
                    } catch (Exception ex) {
                        log.error("Error in sending message into event bus", ex);
                    }
                } else {
                    log.warn("The video file doesn't exist. " + result.getVideoProfile().getFilename());

                    try {
                        DownloadVideoRequest downloadVideoRequest = new DownloadVideoRequest(result.getChatId(), result.getVideoProfile());
                        String jsonToBeSent = gson.toJson(downloadVideoRequest);

                        vertx.eventBus().send(Constants.ADDR_YOUTUBE_DOWNLOAD_VIDEO, jsonToBeSent);
                    } catch (JsonSyntaxException ex) {
                        log.error("Error in parsing video profile json in state", ex);
                    } catch (Exception ex) {
                        log.error("Error in sending message into event bus", ex);
                    }
                } 
            }
        } catch (NullPointerException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void handleLoadStateResultMessage(Message<Object> message) {
        log.info("Load telegram chat state result received.");

        try {
            String json = message.body().toString();
            log.debug("Load telegram chat state result json: " + json);

            Gson gson = new Gson();
            ChatState state = gson.fromJson(json, ChatState.class);

            if (state.getState().equalsIgnoreCase(Constants.STATE_TELEGRAM_CHAT_SENT_YOUTUBE_LINK)) {
                log.info("Sending video info load request for chat id: " + state.getChatId() + " url: \n" + state.getJson() + "\n");

                try {
                    String jsonVideoProfile = state.getJson();
                    VideoProfile videoProfile = gson.fromJson(jsonVideoProfile, VideoProfile.class);
                    LoadVideoRequest loadVideoRequest = new LoadVideoRequest(videoProfile, state.getChatId());

                    String jsonToBeSent = gson.toJson(loadVideoRequest);

                    vertx.eventBus().send(Constants.ADDR_YOUTUBE_VIDEO_LOAD, jsonToBeSent);
                } catch (JsonSyntaxException ex) {
                    log.error("Error in parsing load video request json in state", ex);
                } catch (Exception ex) {
                    log.error("Error in sending message into event bus", ex);
                }

                /*
                log.info("Sending video download request for chat id: " + state.getChatId() + " url: \n" + state.getJson() + "\n");
                
                
                try {
                    String jsonVideoProfile = state.getJson();
                    VideoProfile videoProfile = gson.fromJson(jsonVideoProfile, VideoProfile.class);
                    DownloadVideoRequest downloadVideoRequest = new DownloadVideoRequest(state.getChatId(), videoProfile);
                    
                    String jsonToBeSent = gson.toJson(downloadVideoRequest);
                    
                    vertx.eventBus().send(Constants.ADDR_YOUTUBE_DOWNLOAD_VIDEO, jsonToBeSent);
                } catch (JsonSyntaxException ex) {
                    log.error("Error in parsing video profile json in state", ex);
                } catch (Exception ex) {
                    log.error("Error in sending message into event bus", ex);
                }*/
            }
        } catch (NullPointerException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void handleHook(RoutingContext routingContext) {
        log.info("Hook request..." + routingContext.request().path() + " method: " + routingContext.request().method().name());
        //JsonObject updateJson = routingContext.getBodyAsJson();
        String json = routingContext.getBodyAsString("UTF-8");
        

        try {
            vertx.eventBus().send(Constants.ADDR_LOG_ACCESS, json);
        } catch (Exception ex) {
            log.error("Access log failed", ex);
        }

        log.debug(json);

        Gson gson = new Gson();
        Update update = gson.fromJson(json, Update.class);

        log.info("update: " + update);

        if (update.message() != null && update.message().text() != null) {
            if (Utils.isYouTubeLink(update.message().text())) {
                log.info("Youtube link received: " + update.message().text());

                try {
                    vertx.eventBus().send(Constants.ADDR_YOUTUBE_GET_INFO, json);
                } catch (Exception ex) {
                    log.error("Error in sending message into event bus", ex);
                }
            } else if (Utils.isDownloadCommand(update.message().text())) {
                log.info("Download command received: " + update.message().text());

                ChatState stateRequest = new ChatState(update.message().chat().id());
                String jsonRequest = gson.toJson(stateRequest);

                try {
                    vertx.eventBus().send(Constants.ADDR_LOAD_TELEGRAM_CHAT_STATE, jsonRequest);
                } catch (Exception ex) {
                    log.error("Error in sending message into event bus", ex);
                }
            } else if (Utils.isLongText(update.message().text())) {
                log.info("Long text received: " + update.message().text());
                
                try {
                    vertx.eventBus().send(Constants.ADDR_CREATE_POST, json);
                } catch (Exception ex) {
                    log.error("Error in sending message into event bus", ex);
                }
            }
        }

        HttpServerResponse response = routingContext.response();

        if (routingContext.request().method() == HttpMethod.POST) {
            response.putHeader("content-type", "application/json").end();
        } else {
            sendError(405, response);
        }
    }

    private void handleRoot(RoutingContext routingContext) {
        log.info("Root request..." + routingContext.request().path());
        HttpServerResponse response = routingContext.response();
        try {
            response.putHeader("content-type", "text/html").end(new FileResource("public/html/index.html").getTextContent());
        } catch (IOException ex) {
            log.error("Error in reading index.html", ex);
        }

        try {
            String userAgent = routingContext.request().headers().get("user-agent");
            
            SendTelegramTextRequest request = new SendTelegramTextRequest(userAgent, Constants.USER_ID_EHSUN7B);
            String json = new Gson().toJson(request);
            
            vertx.eventBus().send(Constants.ADDR_SEND_TELEGRAM_MESSAGE, json);
        } catch (Exception ex) {
            log.error("Error in sending message into event bus", ex);
        }
    }

    /*
    private void handleGetProduct(RoutingContext routingContext) {
        String productID = routingContext.request().getParam("productID");
        HttpServerResponse response = routingContext.response();
        if (productID == null) {
            sendError(400, response);
        } else {
            JsonObject product = products.get(productID);
            if (product == null) {
                sendError(404, response);
            } else {
                response.putHeader("content-type", "application/json").end(product.encodePrettily());
            }
        }
    }

    private void handleAddProduct(RoutingContext routingContext) {
        String productID = routingContext.request().getParam("productID");
        HttpServerResponse response = routingContext.response();
        if (productID == null) {
            sendError(400, response);
        } else {
            JsonObject product = routingContext.getBodyAsJson();
            if (product == null) {
                sendError(400, response);
            } else {
                products.put(productID, product);
                response.end();
            }
        }
    }

    private void handleListProducts(RoutingContext routingContext) {
        JsonArray arr = new JsonArray();
        products.forEach((k, v) -> arr.add(v));
        routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
    }*/

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

    /*
    private void addProduct(JsonObject product) {
        products.put(product.getString("id"), product);
    }*/

    private String getKeyStorePassword() {
        return "13621215";
    }

    private int getHttpPort() {
        try {
            return Integer.parseInt(System.getenv(HTTP_PORT_VARIABLE));
        } catch (NullPointerException | NumberFormatException ex) {
            log.error("Error in getting Http Port number. " + HTTP_PORT_VARIABLE);
            return 8080;
        }
    }

    private int getHttpsPort() {
        try {
            return Integer.parseInt(System.getenv(HTTPS_PORT_VARIABLE));
        } catch (NullPointerException | NumberFormatException ex) {
            log.error("Error in getting Https Port number. " + HTTPS_PORT_VARIABLE);
            return 8081;
        }

    }

}

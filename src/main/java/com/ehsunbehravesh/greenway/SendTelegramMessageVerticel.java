package com.ehsunbehravesh.greenway;

import com.ehsunbehravesh.greenway.constant.Constants;
import com.ehsunbehravesh.greenway.telegram.model.request.ParseMode;
import com.ehsunbehravesh.greenway.telegram.model.request.ReplyKeyboardMarkup;
import com.ehsunbehravesh.greenway.telegram.model.request.x.MessageToSend;
import com.ehsunbehravesh.greenway.telegram.model.request.x.VideoToSend;
import com.ehsunbehravesh.greenway.telegram.model.vertx.ChatState;
import com.ehsunbehravesh.greenway.telegram.model.vertx.SendVideoProfileRequest;
import com.ehsunbehravesh.greenway.telegram.model.vertx.SendVideoRequest;
import com.ehsunbehravesh.youtube.model.VideoProfile;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 *
 * @author ehsun7b
 */
public class SendTelegramMessageVerticel extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(SendTelegramMessageVerticel.class);

    @Override
    public void start(Future<Void> startFuture) {

        vertx.eventBus().consumer(Constants.ADDR_SEND_TELEGRAM_MESSAGE, message -> {

            if (message != null && message.body() != null) {
                String text = message.body().toString();
                String apiUrl = "/bot196469941:AAH3ZYKro3NyJadh3N8IBhWsI6SAlfvh75I/sendMessage?chat_id=110303802&text=" + text;

                vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true)).getNow(443, "api.telegram.org", apiUrl, resp -> {
                    System.out.println("Got response " + resp.statusCode());
                    resp.bodyHandler(body -> System.out.println("Got data " + body.toString("UTF-8")));
                });
            }
        });

        vertx.eventBus().consumer(Constants.ADDR_SEND_VIDEO_PROFILE_AS_TELEGRAM_MESSAGE, message -> {
            log.info("Send video profile to user request received");

            String json = message.body().toString();
            Gson gson = new Gson();

            SendVideoProfileRequest request = gson.fromJson(json, SendVideoProfileRequest.class);
            MessageToSend messageToSend = new MessageToSend(request.getUpdate().message().chat().id());
            String text = "You sent a YouTube link:\n"
                    + "Title: *".concat(request.getVideoProfile().getTitle()) + "*\n"
                    + "Duration: ".concat(request.getVideoProfile().getDuration()) + "\n"
                    + "Fil: ".concat(request.getVideoProfile().getFilename()) + "\n"
                    + "Thumbnail: " + request.getVideoProfile().getThumbnailUrl();
            messageToSend.setText(text);
            messageToSend.setParse_mode(ParseMode.Markdown.name());

            ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup(new String[]{"Download", "Cancel"});
            keyboard.setSelective(true);
            keyboard.setOne_time_keyboard(true);
            messageToSend.setReply_markup(keyboard);
            messageToSend.setReply_to_message_id(request.getUpdate().message().messageId());

            json = gson.toJson(messageToSend, MessageToSend.class);
            System.out.println(json);
            vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true)).post(443, Constants.HOST_API, Constants.URL_API + Constants.URL_SEND_MESSAGE, resp -> {
                log.info("Response from " + Constants.URL_SEND_MESSAGE + " :" + resp.statusCode());
                resp.bodyHandler(body -> log.info("Got data " + body.toString("UTF-8")));

                if (resp.statusCode() == 200) {
                    VideoProfile videoProfile = request.getVideoProfile();
                    String requestJson = gson.toJson(videoProfile);
                    ChatState stateRequest = new ChatState(request.getUpdate().message().chat().id(), Constants.STATE_TELEGRAM_CHAT_SENT_YOUTUBE_LINK, requestJson);
                    requestJson = gson.toJson(stateRequest);

                    vertx.eventBus().send(Constants.ADDR_SAVE_TELEGRAM_CHAT_STATE, requestJson);
                }
            }).putHeader(HttpHeaders.CONTENT_LENGTH, json.length() + "")
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .write(json).end();
        });

        // -----------
        vertx.eventBus().consumer(Constants.ADDR_SEND_VIDEO_AS_TELEGRAM_MESSAGE, message -> {
            log.info("Send video to user request received");

            String json = message.body().toString();
            Gson gson = new Gson();

            SendVideoRequest request = gson.fromJson(json, SendVideoRequest.class);

            VideoProfile videoProfile = request.getVideoProfile();
            Long chatId = request.getChatId();

            if (videoProfile.getId() == null) {

            } else {
                log.info("Sending video with file ID: " + videoProfile.getId());
                VideoToSend videoToSend = new VideoToSend(chatId, videoProfile.getId());
                String jsonToSend = gson.toJson(videoToSend);
            
                vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true)).post(443, Constants.HOST_API, Constants.URL_API + Constants.URL_SEND_VIDEO, resp -> {
                    log.info("Response from " + Constants.URL_SEND_VIDEO + " :" + resp.statusCode());
                    resp.bodyHandler(body -> log.info("Got data " + body.toString("UTF-8")));

                    if (resp.statusCode() == 200) {
                        
                    }
                }).putHeader(HttpHeaders.CONTENT_LENGTH, jsonToSend.length() + "")
                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .write(jsonToSend).end();
            }
        });
    }
}

package com.ehsunbehravesh.greenway;

import com.ehsunbehravesh.greenway.config.DatabaseConfig;
import com.ehsunbehravesh.greenway.constant.Constants;
import com.ehsunbehravesh.greenway.telegram.model.Update;
import com.ehsunbehravesh.greenway.telegram.model.vertx.CreatePostRequest;
import com.ehsunbehravesh.greenway.telegram.model.vertx.DownloadVideoRequest;
import com.ehsunbehravesh.greenway.telegram.model.vertx.LoadVideoRequest;
import com.ehsunbehravesh.greenway.telegram.model.vertx.SendTelegramTextRequest;
import com.ehsunbehravesh.greenway.telegram.model.vertx.SendVideoRequest;
import com.ehsunbehravesh.greenway.telegram.utils.Utils;
import com.ehsunbehravesh.post.model.Post;
import com.ehsunbehravesh.youtube.YouTubeDl;
import com.ehsunbehravesh.youtube.model.VideoProfile;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 *
 * @author Ehsun Behravesh <ehsun.behravesh@openet.com>
 */
public class PostCreateVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(PostCreateVerticle.class);

    @Override
    public void start(Future<Void> startFuture) {
        vertx.eventBus().consumer(Constants.ADDR_CREATE_POST, this::handleMessage);
    }

    private void handleMessage(Message<Object> message) {
        log.info("Post create request received.");

        try {
            String json = message.body().toString();
            log.debug("Post create request json: " + json);

            Gson gson = new Gson();
            Update update = gson.fromJson(json, Update.class);

            Post post = new Post(update.message().chat().id());
            post.setUserId(update.message().from().id());
            post.setUsername(update.message().from().username());
            post.setDateTime(ZonedDateTime.now());
            post.setBody(update.message().text());

            vertx.executeBlocking(future -> {

                try {
                    JDBCClient client = JDBCClient.createShared(vertx, DatabaseConfig.INS.databaseConfig());

                    client.getConnection(hndlr -> {
                        if (hndlr.succeeded()) {
                            String sql = "INSERT INTO `tbl_post` (chat_id, user_id, username, time, title, body)"
                                    + " VALUES (?, ?, ?, ?, ?, ?)";
                            JsonArray params = new JsonArray();
                            params.add(post.getChatId());
                            params.add(post.getUserId());
                            params.add(post.getUsername());
                            params.add(Timestamp.from(post.getDateTime().toInstant()).toString());
                            params.add(post.getTitle());
                            params.add(post.getBody());

                            hndlr.result().updateWithParams(sql, params, result -> {
                                if (result.succeeded()) {
                                    log.info("post inserted.");
                                } else {
                                    log.info("post insert failed.", result.cause());
                                }
                            });
                        } else {
                            log.error(hndlr.cause().getMessage());
                        }
                    });

                    future.complete();
                } catch (Exception ex) {
                    log.error("Error in inserting post, ".concat(post.toString()), ex);
                    future.fail(ex);
                }

            }, result -> {

                if (result.succeeded()) {

                    SendTelegramTextRequest request = new SendTelegramTextRequest(Utils.shortenText(post.getBody()), post.getChatId());
                    String jsonSendMessage = new Gson().toJson(request);
                    
                    System.out.println("\n\n\n" + jsonSendMessage + "\n\n\n");

                    vertx.eventBus().send(Constants.ADDR_SEND_TELEGRAM_MESSAGE, jsonSendMessage);
                }
            });
        } catch (NullPointerException ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}

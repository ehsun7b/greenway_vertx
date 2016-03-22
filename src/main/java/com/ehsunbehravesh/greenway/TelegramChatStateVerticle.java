package com.ehsunbehravesh.greenway;

import com.ehsunbehravesh.greenway.config.DatabaseConfig;
import com.ehsunbehravesh.greenway.constant.Constants;
import com.ehsunbehravesh.greenway.telegram.model.vertx.SaveChatStateRequest;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;

/**
 *
 * @author ehsun7b
 */
public class TelegramChatStateVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(TelegramChatStateVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(Constants.ADDR_SAVE_TELEGRAM_CHAT_STATE, this::handleMessage);
    }

    private void handleMessage(Message<Object> message) {
        log.info("Save telegram chat state request received.");

        try {
            String json = message.body().toString();
            log.debug("Save telegram chat state request json: " + json);

            Gson gson = new Gson();
            SaveChatStateRequest request = gson.fromJson(json, SaveChatStateRequest.class);

            JDBCClient client = JDBCClient.createShared(vertx, DatabaseConfig.INS.databaseConfig());

            client.getConnection(hndlr -> {
                if (hndlr.succeeded()) {
                    String sql = "INSERT INTO `tbl_chat_state` (chat_id, state, json) values(?, ?, ?)";
                    JsonArray params = new JsonArray();
                    params.add(request.getChatId());
                    params.add(request.getState());
                    params.add(request.getJson());

                    hndlr.result().updateWithParams(sql, params, result -> {
                        if (result.succeeded()) {
                            log.info("Chat state saved.");
                        } else {
                            log.info("Chat sate save failed. ", result.cause());
                        }
                    });
                } else {
                    System.out.println(hndlr.cause().getMessage());
                }
            });

        } catch (NullPointerException ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}

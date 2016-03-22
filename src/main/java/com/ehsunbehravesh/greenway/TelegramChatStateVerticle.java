package com.ehsunbehravesh.greenway;

import com.ehsunbehravesh.greenway.config.DatabaseConfig;
import com.ehsunbehravesh.greenway.constant.Constants;
import com.ehsunbehravesh.greenway.telegram.model.vertx.ChatState;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import java.util.List;

/**
 *
 * @author ehsun7b
 */
public class TelegramChatStateVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(TelegramChatStateVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(Constants.ADDR_SAVE_TELEGRAM_CHAT_STATE, this::handleSaveMessage);
        vertx.eventBus().consumer(Constants.ADDR_LOAD_TELEGRAM_CHAT_STATE, this::handleLoadMessage);
    }

    private void handleSaveMessage(Message<Object> message) {
        log.info("Save telegram chat state request received.");

        try {
            String json = message.body().toString();
            log.debug("Save telegram chat state request json: " + json);

            Gson gson = new Gson();
            ChatState request = gson.fromJson(json, ChatState.class);

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
    
    
    private void handleLoadMessage(Message<Object> message) {
        log.info("Load telegram chat state request received.");

        try {
            String json = message.body().toString();
            log.debug("Load telegram chat state request json: " + json);

            Gson gson = new Gson();
            ChatState request = gson.fromJson(json, ChatState.class);

            JDBCClient client = JDBCClient.createShared(vertx, DatabaseConfig.INS.databaseConfig());

            client.getConnection(hndlr -> {
                if (hndlr.succeeded()) {
                    String sql = "SELECT * FROM `tbl_chat_state` WHERE `chat_id` = ? ORDER BY `id` DESC";
                    JsonArray params = new JsonArray();
                    params.add(request.getChatId());
                    
                    hndlr.result().queryWithParams(sql, params, (AsyncResult<ResultSet> result) -> {
                        if (result.succeeded()) {
                            log.info("Chat state loaded.");
                            ResultSet resultSet = result.result();
                            
                            List<JsonObject> rows = resultSet.getRows();
                            
                            if (rows.size() <= 0) {
                                log.info("No state rows found for chat id: " + request.getChatId());
                            } else {
                                JsonObject row = rows.get(0);
                                String jsonStr = row.getString("json");
                                Long chatId = row.getLong("chat_id");
                                String state = row.getString("state");
                                
                                ChatState stateResult = new ChatState(chatId, state, jsonStr);
                                jsonStr = gson.toJson(stateResult);
                                vertx.eventBus().send(Constants.ADDR_LOAD_TELEGRAM_CHAT_STATE_RESULT, jsonStr);
                            }
                        } else {
                            log.info("Chat state load failed. ", result.cause());
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

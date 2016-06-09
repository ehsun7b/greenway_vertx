package com.ehsunbehravesh.greenway;

import com.ehsunbehravesh.greenway.config.DatabaseConfig;
import com.ehsunbehravesh.greenway.constant.Constants;
import com.ehsunbehravesh.greenway.telegram.model.vertx.LoadVideoRequest;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;

/**
 *
 * @author ehsun7b
 */
public class YouTubeVideoSaveVerticle extends AbstractVerticle {
    
    private static final Logger log = LoggerFactory.getLogger(YouTubeVideoSaveVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(Constants.ADDR_YOUTUBE_VIDEO_SAVE, this::handleSaveMessage);
        vertx.eventBus().consumer(Constants.ADDR_YOUTUBE_VIDEO_UPDATE_FILE_ID, this::handleUpdateFileIdMessage);
    }
    
    private void handleSaveMessage(Message<Object> message) {
        log.info("Save video request received.");

        try {
            String json = message.body().toString();
            log.debug("Save video request json: " + json);

            Gson gson = new Gson();
            LoadVideoRequest request = gson.fromJson(json, LoadVideoRequest.class);

            JDBCClient client = JDBCClient.createShared(vertx, DatabaseConfig.INS.databaseConfig());

            client.getConnection(hndlr -> {
                if (hndlr.succeeded()) {
                    String sql = "INSERT INTO `tbl_youtube_video` (`id`, `url`, `filename`) VALUES (?, ?, ?)";
                    JsonArray params = new JsonArray();
                    params.add(request.getVideoProfile().getId());
                    params.add(request.getVideoProfile().getUrl());
                    params.add(request.getVideoProfile().getFilename());
                    
                    hndlr.result().updateWithParams(sql, params, result -> {
                        if (result.succeeded()) {
                            log.info("Video profile saved.");                            
                        } else {
                            log.info("YouTube video save failed. ", result.cause());
                        }
                    });
                } else {
                    log.error(hndlr.cause().getMessage());
                }
            });

        } catch (NullPointerException ex) {
            log.error(ex.getMessage(), ex);
        }
    }
    
    private void handleUpdateFileIdMessage(Message<Object> message) {
        log.info("Update video file id request received.");

        try {
            String json = message.body().toString();
            log.debug("Update video file id request json: " + json);

            Gson gson = new Gson();
            LoadVideoRequest request = gson.fromJson(json, LoadVideoRequest.class);

            JDBCClient client = JDBCClient.createShared(vertx, DatabaseConfig.INS.databaseConfig());

            client.getConnection(hndlr -> {
                if (hndlr.succeeded()) {
                    String sql = "UPDATE `tbl_youtube_video` SET `file_id` = ? WHERE `id` = ?";
                    JsonArray params = new JsonArray();
                    params.add(request.getVideoProfile().getId());
                    params.add(request.getVideoProfile().getId());
                    
                    hndlr.result().updateWithParams(sql, params, result -> {
                        if (result.succeeded()) {
                            log.info("Video file id updated.");                            
                        } else {
                            log.info("YouTube video file id update failed. ", result.cause());
                        }
                    });
                } else {
                    log.error(hndlr.cause().getMessage());
                }
            });

        } catch (NullPointerException ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}

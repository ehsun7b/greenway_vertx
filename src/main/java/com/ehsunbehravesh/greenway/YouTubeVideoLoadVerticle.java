package com.ehsunbehravesh.greenway;

import com.ehsunbehravesh.greenway.config.DatabaseConfig;
import com.ehsunbehravesh.greenway.constant.Constants;
import com.ehsunbehravesh.greenway.telegram.model.vertx.LoadVideoRequest;
import com.ehsunbehravesh.youtube.model.VideoProfile;
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
public class YouTubeVideoLoadVerticle extends AbstractVerticle {
    
    private static final Logger log = LoggerFactory.getLogger(YouTubeVideoLoadVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(Constants.ADDR_YOUTUBE_VIDEO_LOAD, this::handleLoadMessage);
    }
    
    private void handleLoadMessage(Message<Object> message) {
        log.info("Load video request received.");

        try {
            String json = message.body().toString();
            log.debug("Load video request json: " + json);

            Gson gson = new Gson();
            LoadVideoRequest request = gson.fromJson(json, LoadVideoRequest.class);

            JDBCClient client = JDBCClient.createShared(vertx, DatabaseConfig.INS.databaseConfig());

            client.getConnection(hndlr -> {
                if (hndlr.succeeded()) {
                    String sql = "SELECT * FROM `tbl_youtube_video` WHERE `id` = ?";
                    JsonArray params = new JsonArray();
                    params.add(request.getVideoProfile().getId());
                    
                    hndlr.result().queryWithParams(sql, params, (AsyncResult<ResultSet> result) -> {
                        if (result.succeeded()) {
                            log.info("Video profile loaded.");
                            ResultSet resultSet = result.result();
                            
                            List<JsonObject> rows = resultSet.getRows();
                            
                            if (rows.size() <= 0) {
                                log.info("No video profile found for id: " + request.getChatId());
                            } else {
                                JsonObject row = rows.get(0);
                                String id = row.getString("id");
                                String url = row.getString("url");
                                String filename = row.getString("filename");                                
                                
                                VideoProfile videoProfile = new VideoProfile(url);
                                videoProfile.setId(id);
                                videoProfile.setFilename(filename);
                                
                                LoadVideoRequest loadResult = new LoadVideoRequest(videoProfile, request.getChatId());
                                String json2 = gson.toJson(loadResult);
                                vertx.eventBus().send(Constants.ADDR_YOUTUBE_VIDEO_LOAD_RESULT, json2);
                            }
                        } else {
                            log.info("YouTube video load failed. ", result.cause());
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

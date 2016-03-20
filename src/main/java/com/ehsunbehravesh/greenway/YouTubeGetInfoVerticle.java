package com.ehsunbehravesh.greenway;

import com.ehsunbehravesh.greenway.constant.Constants;
import com.ehsunbehravesh.greenway.telegram.model.Update;
import com.ehsunbehravesh.youtube.YouTubeDl;
import com.ehsunbehravesh.youtube.model.VideoProfile;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

/**
 *
 * @author ehsun7b
 */
public class YouTubeGetInfoVerticle extends AbstractVerticle {
    
    private static final Logger log = LoggerFactory.getLogger(YouTubeGetInfoVerticle.class);
    
    @Override
    public void start(Future<Void> startFuture) {
        vertx.eventBus().consumer(Constants.ADDR_YOUTUBE_GET_INFO, this::handleMessage);
    }
    
    private void handleMessage(Message<Object> message) {
        log.info("YouTube get info request received.");

        try {            
            String json = message.body().toString();
            log.debug("YouTube get info request update json: " + json);

            Gson gson = new Gson();
            Update update = gson.fromJson(json, Update.class);
            
            vertx.executeBlocking(future -> {
                String youtubeUrl = update.message().text().trim();

                YouTubeDl youTubeDl = new YouTubeDl();
                try {
                    VideoProfile profile = youTubeDl.getProfile(youtubeUrl);
                    future.complete(profile);
                } catch (IOException | InterruptedException ex) {
                    log.error("Error in getting video info, ".concat(youtubeUrl), ex);
                    future.fail(ex);
                } 
                
            }, result -> {
                if (result.succeeded()) {
                    VideoProfile videoProfile = (VideoProfile) result.result();
                    String jsonVideoProfile = gson.toJson(videoProfile);
                    vertx.eventBus().send(Constants.ADDR_SEND_VIDEO_PROFILE_AS_TELEGRAM_MESSAGE, jsonVideoProfile);
                }
            });
        } catch (NullPointerException ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}

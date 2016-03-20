package com.ehsunbehravesh.greenway;

import com.ehsunbehravesh.greenway.constant.Constants;
import com.ehsunbehravesh.greenway.telegram.model.Update;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author ehsun7b
 */
public class YouTubeDownloadVideoVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(YouTubeDownloadVideoVerticle.class);

    private static final String VIDEO_DIR_VARIABLE = "GREENWAY_VIDEO_DIR";

    @Override
    public void start(Future<Void> startFuture) {
        vertx.eventBus().consumer(Constants.ADDR_YOUTUBE_DOWNLOAD_VIDEO, this::handleMessage);
    }

    private void handleMessage(Message<Object> message) {
        log.info("YouTube download request received.");

        try {/*
            Update update = message.body();
            log.debug("YouTube download request update: " + update);

            vertx.executeBlocking(future -> {
                String youtubeUrl = update.message().text().trim();

                String videoDir = System.getenv(VIDEO_DIR_VARIABLE);

                if (videoDir == null) {
                    future.fail(new Exception("Video DIR is not set. ".concat(VIDEO_DIR_VARIABLE)));
                } else {
                    //try {
                        File videoDirFile = new File(videoDir);
                        
                        if (videoDirFile.isDirectory() && videoDirFile.exists()) {
                            
                        } else {
                            future.fail(new FileNotFoundException(videoDir + " not found or not a directory!"));
                        }
                    //} catch (IOException ex) {

                    //}
                }

            }, result -> {

            });*/
        } catch (NullPointerException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}

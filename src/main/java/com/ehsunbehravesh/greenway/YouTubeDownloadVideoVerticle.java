package com.ehsunbehravesh.greenway;

import com.ehsunbehravesh.greenway.constant.Constants;
import com.ehsunbehravesh.greenway.telegram.model.vertx.DownloadVideoRequest;
import com.ehsunbehravesh.youtube.YouTubeDl;
import com.ehsunbehravesh.youtube.model.VideoProfile;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 *
 * @author ehsun7b
 */
public class YouTubeDownloadVideoVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(YouTubeDownloadVideoVerticle.class);

    @Override
    public void start(Future<Void> startFuture) {
        vertx.eventBus().consumer(Constants.ADDR_YOUTUBE_DOWNLOAD_VIDEO, this::handleMessage);
    }

    private void handleMessage(Message<Object> message) {
        log.info("YouTube download request received.");

        try {
            String json = message.body().toString();
            log.debug("YouTube download request json: " + json);

            Gson gson = new Gson();
            DownloadVideoRequest downloadRequest = gson.fromJson(json, DownloadVideoRequest.class);

            vertx.executeBlocking(future -> {                
                YouTubeDl youTubeDl = new YouTubeDl();
                VideoProfile videoProfile = downloadRequest.getVideoProfile();
                try {
                    youTubeDl.download(videoProfile);
                    
                    future.complete();
                } catch (Exception ex) {
                    log.error("Error in downloading video, ".concat(videoProfile.getUrl()), ex);
                    future.fail(ex);
                }

            }, result -> {
                /*
                if (result.succeeded()) {
                    VideoProfile videoProfile = (VideoProfile) result.result();
                    SendVideoProfileRequest sendVideoProfileRequest = new SendVideoProfileRequest(update, videoProfile);
                    String jsonRequest = gson.toJson(sendVideoProfileRequest);
                    vertx.eventBus().send(Constants.ADDR_SEND_VIDEO_PROFILE_AS_TELEGRAM_MESSAGE, jsonRequest);
                }*/
            });
        } catch (NullPointerException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}

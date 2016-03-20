package com.ehsunbehravesh.greenway;

import com.ehsunbehravesh.greenway.constant.Constants;
import com.ehsunbehravesh.greenway.telegram.model.Update;
import com.ehsunbehravesh.greenway.telegram.model.request.ParseMode;
import com.ehsunbehravesh.greenway.telegram.model.request.x.MessageToSend;
import com.ehsunbehravesh.youtube.model.VideoProfile;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClientOptions;
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
            System.out.println("1 received message.body() = "
                    + message.body());

            String text = message.body().toString();
            String apiUrl = "/bot196469941:AAH3ZYKro3NyJadh3N8IBhWsI6SAlfvh75I/sendMessage?chat_id=110303802&text=hello from greenway";

            String messageJson = "{\"chat_id\": 110303802, \"text\": \"Hello from Green way. " + text + "\"}";

            vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true)).getNow(443, "api.telegram.org", apiUrl, resp -> {
                System.out.println("Got response " + resp.statusCode());
                resp.bodyHandler(body -> System.out.println("Got data " + body.toString("ISO-8859-1")));
            });
        });

        vertx.eventBus().consumer(Constants.ADDR_SEND_VIDEO_PROFILE_AS_TELEGRAM_MESSAGE, message -> {
            log.info("Send video profile to user request received");

            SendVideoProfileRequest request = (SendVideoProfileRequest) message.body();
            MessageToSend messageToSend = new MessageToSend(request.update.message().chat().id());
            String text = "You sent a YouTube video:\n"
                    + "\nTitle: <b>".concat(request.videoProfile.getTitle()) + "</b>\n"
                    + "Duration: <b>".concat(request.videoProfile.getDuration()) + "</b>\n"
                    + "Filename: <b>".concat(request.videoProfile.getFilename()) + "</b>\n"
                    + "Thumbnail: <a href=\"" + request.videoProfile.getThumbnailUrl() + "\">".concat(request.videoProfile.getThumbnailUrl()) + "</b>\n";
            messageToSend.setText(text);
            messageToSend.setParse_mode(ParseMode.HTML.name());
            
            Gson gson = new Gson();
            String json = gson.toJson(messageToSend, MessageToSend.class);
            
            vertx.createHttpClient(new HttpClientOptions().setSsl(true).setTrustAll(true)).getNow(443, Constants.HOST_API, Constants.URL_API, resp -> {
                log.info("Response from " + Constants.URL_SEND_MESSAGE + " :" + resp.statusCode());
                resp.bodyHandler(body -> log.debug("Got data " + body.toString("UTF-8")));
            });
        });
    }

    public class SendVideoProfileRequest {

        protected final Update update;
        protected final VideoProfile videoProfile;

        public SendVideoProfileRequest(Update update, VideoProfile videoProfile) {
            this.update = update;
            this.videoProfile = videoProfile;
        }

        public Update getUpdate() {
            return update;
        }

        public VideoProfile getVideoProfile() {
            return videoProfile;
        }

    }
}

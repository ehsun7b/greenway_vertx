package com.ehsunbehravesh.greenway.telegram.model.vertx;

import com.ehsunbehravesh.greenway.telegram.model.Update;
import com.ehsunbehravesh.youtube.model.VideoProfile;

/**
 *
 * @author ehsun7b
 */
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

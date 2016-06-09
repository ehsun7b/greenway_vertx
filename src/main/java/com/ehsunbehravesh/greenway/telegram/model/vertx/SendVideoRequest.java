package com.ehsunbehravesh.greenway.telegram.model.vertx;

import com.ehsunbehravesh.youtube.model.VideoProfile;

/**
 *
 * @author ehsun7b
 */
public class SendVideoRequest extends Request {

    protected final VideoProfile videoProfile;

    public SendVideoRequest(VideoProfile videoProfile, Long chatId) {
        super(chatId);
        this.videoProfile = videoProfile;
    }

    public VideoProfile getVideoProfile() {
        return videoProfile;
    }

    
}

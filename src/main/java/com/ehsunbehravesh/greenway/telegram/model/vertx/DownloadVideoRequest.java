package com.ehsunbehravesh.greenway.telegram.model.vertx;

import com.ehsunbehravesh.youtube.model.VideoProfile;

/**
 *
 * @author ehsun7b
 */
public class DownloadVideoRequest {

    protected final Long chatId;
    protected final VideoProfile videoProfile;

    public DownloadVideoRequest(Long chatId, VideoProfile videoProfile) {
        this.chatId = chatId;
        this.videoProfile = videoProfile;
    }

    public Long getChatId() {
        return chatId;
    }

    public VideoProfile getVideoProfile() {
        return videoProfile;
    }
}

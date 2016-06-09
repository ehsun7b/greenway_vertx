package com.ehsunbehravesh.greenway.telegram.model.request.x;

import com.ehsunbehravesh.greenway.telegram.model.request.Keyboard;

/**
 *
 * @author ehsun7b
 */
public class VideoToSend {

    protected Long chat_id;
    protected String video;
    protected Integer duration;
    protected Integer width;
    protected Integer height;
    protected String caption;
    protected Boolean disable_notification;
    protected Integer reply_to_message_id;
    protected Keyboard reply_markup;

    public VideoToSend(Long chat_id, String video) {
        this.chat_id = chat_id;
        this.video = video;
    }
    
    public Long getChat_id() {
        return chat_id;
    }

    public void setChat_id(Long chat_id) {
        this.chat_id = chat_id;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Boolean getDisable_notification() {
        return disable_notification;
    }

    public void setDisable_notification(Boolean disable_notification) {
        this.disable_notification = disable_notification;
    }

    public Integer getReply_to_message_id() {
        return reply_to_message_id;
    }

    public void setReply_to_message_id(Integer reply_to_message_id) {
        this.reply_to_message_id = reply_to_message_id;
    }

    public Keyboard getReply_markup() {
        return reply_markup;
    }

    public void setReply_markup(Keyboard reply_markup) {
        this.reply_markup = reply_markup;
    }
    
    
}

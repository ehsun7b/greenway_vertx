package com.ehsunbehravesh.greenway.telegram.model.request.x;

import com.ehsunbehravesh.greenway.telegram.model.request.Keyboard;
import com.ehsunbehravesh.greenway.telegram.model.request.ReplyKeyboardMarkup;

/**
 *
 * @author ehsun7b
 */
public class MessageToSend {

    protected Long chat_id;
    protected String text;
    protected String parse_mode;
    protected Boolean disable_web_page_preview;
    protected Boolean disable_notification;
    protected Integer reply_to_message_id;
    protected Keyboard reply_markup;

    public MessageToSend(Long chat_id, String text) {
        this.chat_id = chat_id;
        this.text = text;
    }

    public MessageToSend(Long chat_id) {
        this.chat_id = chat_id;
    }

    public MessageToSend() {
    }

    public Long getChat_id() {
        return chat_id;
    }

    public void setChat_id(Long chat_id) {
        this.chat_id = chat_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getParse_mode() {
        return parse_mode;
    }

    public void setParse_mode(String parse_mode) {
        this.parse_mode = parse_mode;
    }

    public Boolean getDisable_web_page_preview() {
        return disable_web_page_preview;
    }

    public void setDisable_web_page_preview(Boolean disable_web_page_preview) {
        this.disable_web_page_preview = disable_web_page_preview;
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

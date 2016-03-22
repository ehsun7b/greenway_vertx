package com.ehsunbehravesh.greenway.telegram.model.vertx;

/**
 *
 * @author ehsun7b
 */
public class SaveChatStateRequest {
    protected final Long chatId;
    protected final String state;
    protected final String json;

    public SaveChatStateRequest(Long chatId, String state) {
        this.chatId = chatId;
        this.state = state;
        this.json = null;
    }

    public SaveChatStateRequest(Long chatId, String state, String json) {
        this.chatId = chatId;
        this.state = state;
        this.json = json;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getState() {
        return state;
    }

    public String getJson() {
        return json;
    }
    
    
}

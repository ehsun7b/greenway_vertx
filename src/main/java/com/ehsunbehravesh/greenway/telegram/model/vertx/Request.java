package com.ehsunbehravesh.greenway.telegram.model.vertx;

/**
 *
 * @author ehsun7b
 */
public class Request {

    protected final Long chatId;

    public Request(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }
    
    
}

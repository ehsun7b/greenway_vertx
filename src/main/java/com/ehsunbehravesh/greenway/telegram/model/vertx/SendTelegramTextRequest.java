package com.ehsunbehravesh.greenway.telegram.model.vertx;

/**
 *
 * @author Ehsun Behravesh <ehsun.behravesh@openet.com>
 */
public class SendTelegramTextRequest extends Request {
    
    protected final String body;

    public SendTelegramTextRequest(String body, Long chatId) {
        super(chatId);
        this.body = body;
    }

    public String getBody() {
        return body;
    }    
}

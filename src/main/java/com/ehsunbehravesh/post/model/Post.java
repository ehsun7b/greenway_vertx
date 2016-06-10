package com.ehsunbehravesh.post.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 *
 * @author Ehsun Behravesh <ehsun.behravesh@openet.com>
 */
public class Post implements Serializable {
    
    protected Long id;
    protected final Long chatId;
    protected Integer userId;
    protected String username;
    protected ZonedDateTime dateTime;
    protected String title;
    protected String body;

    @Override
    public String toString() {
        return "Post{" + "id=" + id + ", chatId=" + chatId + ", userId=" + userId + ", username=" + username + ", dateTime=" + dateTime + ", title=" + title + ", body=" + body + '}';
    }
    
    public Post(Long chatId) {
        this.chatId = chatId;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getChatId() {
        return chatId;
    }
}

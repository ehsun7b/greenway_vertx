package com.ehsunbehravesh.youtube.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author Ehsun Behravesh <ehsun.behravesh@openet.com>
 */
public class Post implements Serializable {
    
    protected Long id;
    protected final Long chatId;
    protected Integer userId;
    protected String username;
    protected LocalDateTime dateTime;
    protected String title;
    protected String body;

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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
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
    
    
}

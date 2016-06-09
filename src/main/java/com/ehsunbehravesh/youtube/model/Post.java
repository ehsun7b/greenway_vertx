package com.ehsunbehravesh.youtube.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author Ehsun Behravesh <ehsun.behravesh@openet.com>
 */
public class Post implements Serializable {
    
    protected Long id;
    protected Long userId;
    protected LocalDateTime dateTime;
    protected String title;
    protected String body;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

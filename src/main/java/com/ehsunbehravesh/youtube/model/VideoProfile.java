package com.ehsunbehravesh.youtube.model;

import java.io.Serializable;

/**
 *
 * @author ehsun7b
 */
public class VideoProfile implements Serializable {

    /**
     * Youtube id, e.g 9jZ0Zff_2Do
     */
    protected String id;

    /**
     * Video title
     */
    protected String title;

    /**
     * Original thumbnail url
     */
    protected String thumbnailUrl;

    /**
     * Local thumbnail saved file
     */
    protected String thumbnailFile;

    /**
     * Original url entered by user
     */
    protected String url;

    /**
     * Duration in hh:mm:ss
     */
    protected String duration;

    /**
     * Original description
     */
    protected String description;

    /**
     * Original format information
     */
    protected String format;

    /**
     * Original file name
     */
    protected String filename;
    
    /**
     * File ID on Telegram servers
     */
    protected String fileId;

    @Override
    public String toString() {
        return "VideoProfile{" + "id=" + id + ", title=" + title + ", thumbnailUrl=" + thumbnailUrl + ", thumbnailFile=" + thumbnailFile + ", url=" + url + ", duration=" + duration + ", description=" + description + ", format=" + format + ", filename=" + filename + ", fileId=" + fileId + '}';
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    
    public VideoProfile(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getThumbnailFile() {
        return thumbnailFile;
    }

    public void setThumbnailFile(String thumbnailFile) {
        this.thumbnailFile = thumbnailFile;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}

package com.ehsunbehravesh.greenway.telegram.model.vertx;

import com.ehsunbehravesh.post.model.Post;

/**
 *
 * @author Ehsun Behravesh <ehsun.behravesh@openet.com>
 */
public class CreatePostRequest extends Request {
    
    private Post post;
    
    public CreatePostRequest(Long chatId) {
        super(chatId);
    }

    public CreatePostRequest(Post post, Long chatId) {
        super(chatId);
        this.post = post;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
    
    
    
}

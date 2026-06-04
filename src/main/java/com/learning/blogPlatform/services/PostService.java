package com.learning.blogPlatform.services;

import com.learning.blogPlatform.entities.Post;
import com.learning.blogPlatform.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post createPost(String userName, Post post){
        post.setUserName(userName);
        return postRepository.save(post);
    }

    public Post findPost(String id){
        return postRepository.findById(id).orElse(null);
    }

    public Post getPost(String userName, String id){
        Post post = findPost(id);
        if(post.getUserName().equals(userName)) return post;
        return null;
    }

    public Post updatePost(String userName, String id, Post newPost){
        Post oldPost = findPost(id);
        if(oldPost == null) return null;

        if(!oldPost.getUserName().equals(userName)) return null;

        if(newPost.getCaption() != null) oldPost.setCaption(newPost.getCaption());
        if(newPost.getImageUrl() != null) oldPost.setImageUrl(newPost.getImageUrl());

        return createPost(userName, oldPost);
    }

    public void deletePost(String userName, String id){
        Post post = findPost(id);
        if(!post.getUserName().equals(userName)) return;

        postRepository.deleteById(id);
    }

}

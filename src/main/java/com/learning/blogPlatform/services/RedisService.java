package com.learning.blogPlatform.services;

import com.learning.blogPlatform.entities.Post;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {


    private final RedisTemplate<String, Post> redisTemplate;

    public RedisService(RedisTemplate<String, Post> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void savePost(Post post) {
        redisTemplate.opsForValue().set("post:" + post.getId(), post);
    }

    public Post getPost(String postId) {
        return redisTemplate.opsForValue().get("post:" + postId);
    }

    public void deletePost(String postId) {
        redisTemplate.delete("post:" + postId);
    }

//    public List<Post> getAllPosts() {
//        return redisTemplate.opsForValue().multiGet(redisTemplate.keys("post:*"));
//    }
}

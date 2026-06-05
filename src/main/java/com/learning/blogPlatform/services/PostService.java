package com.learning.blogPlatform.services;

import com.learning.blogPlatform.entities.Comment;
import com.learning.blogPlatform.entities.Like;
import com.learning.blogPlatform.entities.Post;
import com.learning.blogPlatform.enums.TargetType;
import com.learning.blogPlatform.repositories.LikeRepository;
import com.learning.blogPlatform.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    public Post createPost(String userName, Post post){
        post.setUserName(userName);
        return postRepository.save(post);
    }

    public Post savePost(Post post){
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

        return savePost(oldPost);
    }

    public void deletePost(String userName, String id){
        Post post = findPost(id);
        if(!post.getUserName().equals(userName)) return;

        postRepository.deleteById(id);
    }

    public List<String> likedByUsers(String postId) {
        return likeRepository.findUserNamesByPostId(TargetType.POST, postId);
    }

//    public List<Post> likedOnPosts(String userName) {
//        List<Like> likes = likeRepository.findByUserName(userName);
//
//        List<Post> list = new ArrayList<>();
//        for (Like like : likes) {
//            String postId = like.getPostId();
//            Post post = findPost(postId);
//            list.add(post);
//        }
//        return list;
//    }

    @Transactional
    public boolean likePost(String userName, String postId){
        Post post = findPost(postId);
        if(post == null) {
            throw new IllegalArgumentException("Post not found");
        }
        boolean exist = likeRepository.existsByTargetIdAndTargetTypeAndUserName(
                postId, TargetType.POST,userName);

        if(exist){
            post.setLikeCount(post.getLikeCount() - 1);
            likeRepository.deleteByTargetIdAndTargetTypeAndUserName(postId, TargetType.POST, userName);
            savePost(post);
            return false;
        }

        Like like = new Like();
        like.setTargetId(postId);
        like.setTargetType(TargetType.POST);
        like.setUserName(userName);

        post.setLikeCount(post.getLikeCount() + 1);
        savePost(post);
        likeRepository.save(like);
        return true;
    }
}

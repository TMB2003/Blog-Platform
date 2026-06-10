package com.learning.blogPlatform.services;

import com.learning.blogPlatform.entities.Like;
import com.learning.blogPlatform.entities.NotificationEvent;
import com.learning.blogPlatform.entities.Post;
import com.learning.blogPlatform.entities.User;
import com.learning.blogPlatform.enums.TargetType;
import com.learning.blogPlatform.repositories.LikeRepository;
import com.learning.blogPlatform.repositories.PostRepository;
import com.learning.blogPlatform.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;


    public String uploadImage(MultipartFile file) throws IOException {
        return cloudinaryService.uploadImage(file);
    }

    public Post createPost(String userName, String caption, MultipartFile file) throws IOException {
        Post post = new Post();
        post.setUserName(userName);
        post.setCaption(caption);

        if(file != null && !file.isEmpty()){
            String imageUrl = uploadImage(file);
            post.setImageUrl(imageUrl);
        }

        Post savedPost = postRepository.save(post);
        redisService.savePost(savedPost);

        User user = userRepository.findByUserName(post.getUserName());

        NotificationEvent event = new NotificationEvent(
                user.getEmail(),
                "Post created Successfully",
                userName + " your post: " + post.getCaption()
        );
//        kafkaProducerService.sendPostNotification(event);
        emailService.sendMail(event);
        return savedPost;
    }

    public Post savePost(Post post){
        Post savedPost = postRepository.save(post);
        redisService.savePost(savedPost);
        return savedPost;
    }

    public Post findPost(String postId){
        Post post = redisService.getPost(postId);
        if(post != null) return post;
        post = postRepository.findById(postId).orElse(null);
        if(post != null) redisService.savePost(post); // ✅ null check!
        return post;
    }

    public Post getPost( String id){
        return findPost(id);
    }

    public Post updatePost(String userName, String id, String newCaption, MultipartFile file) throws IOException {
        Post oldPost = findPost(id);
        if(oldPost == null) return null;

        if(!oldPost.getUserName().equals(userName)) return null;

        if(newCaption != null && !newCaption.trim().isEmpty()) oldPost.setCaption(newCaption);
        if (file != null && !file.isEmpty()) {
            String url = uploadImage(file);
            oldPost.setImageUrl(url);
        }

        return savePost(oldPost);
    }

    public void deletePost(String userName, String postId){
        Post post = findPost(postId);
        if(!post.getUserName().equals(userName)) return;
        redisService.deletePost(postId);
        postRepository.deleteById(postId);
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
            likeRepository.deleteByTargetIdAndTargetTypeAndUserName(postId.toString(), TargetType.POST, userName);
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

        User user = userRepository.findByUserName(post.getUserName());

        NotificationEvent event = new NotificationEvent(
                user.getEmail(),
                userName + " liked your post",
                userName + " liked your post: " + post.getCaption()
        );
//        kafkaProducerService.sendLikeNotification(event);
        emailService.sendMail(event);
        return true;
    }
}

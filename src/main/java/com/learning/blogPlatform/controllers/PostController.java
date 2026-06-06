package com.learning.blogPlatform.controllers;

import com.learning.blogPlatform.entities.Post;
import com.learning.blogPlatform.services.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;


    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> createPost(
            @RequestPart("file") MultipartFile file,
            @RequestParam("caption") String caption){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try{
            Post post = postService.createPost(userName, caption, file);
            return new ResponseEntity<>(post, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Post not created: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable String postId){
        try{
            Post post = postService.getPost(postId);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Post not found: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable String postId, @RequestBody Post newPost){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try{
            Post post = postService.updatePost(userName, postId, newPost);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in updating Post: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable String postId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try{
            postService.deletePost(userName, postId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in deleteing post: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<List<String>> likedByUsers(@PathVariable String postId){
        try{
            List<String> list = postService.likedByUsers(postId);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in getting Likes: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable String postId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try{
            boolean liked = postService.likePost(userName, postId);
            if(liked) return new ResponseEntity<>("Liked the Post", HttpStatus.CREATED);
            return new ResponseEntity<>("DisLiked the Post", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in Liking Post: ", e);
            return new ResponseEntity<>("Error in liking the Post", HttpStatus.NOT_FOUND);
        }
    }
}

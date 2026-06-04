package com.learning.blogPlatform.controllers;

import com.learning.blogPlatform.entities.Post;
import com.learning.blogPlatform.services.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;


    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody Post post){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try{
            post = postService.createPost(userName, post);
            return new ResponseEntity<>(post, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Post not created: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable String id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try{
            Post post = postService.getPost(userName, id);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Post not found: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable String id, @RequestBody Post newPost){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try{
            Post post = postService.updatePost(userName, id, newPost);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in updating Post: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try{
            postService.deletePost(userName, id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in deleteing post: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

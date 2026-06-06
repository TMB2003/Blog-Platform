package com.learning.blogPlatform.controllers;


import com.learning.blogPlatform.entities.Comment;
import com.learning.blogPlatform.enums.TargetType;
import com.learning.blogPlatform.services.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;


    @PostMapping("/{targetId}/create")
    public ResponseEntity<Comment> createComment(@PathVariable String targetId, @RequestBody Comment comment){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try{
            comment = commentService.createComment(userName, targetId, comment);
            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Comment not created: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{targetType}/{targetId}")
    public ResponseEntity<List<Comment>> getAll(@PathVariable TargetType targetType, @PathVariable String targetId){
        try{
            List<Comment> list = commentService.getAll(targetId, targetType);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in finding Comments: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> getComment(@PathVariable Long commentId){
        try{
            Comment comment = commentService.findComment(commentId);
            if(comment == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(comment, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Comment not found: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long commentId, @RequestBody Comment newComment){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try{
            Comment comment = commentService.updateComment(userName, commentId, newComment);
            return new ResponseEntity<>(comment, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in updating Comment: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try{
            commentService.deleteComment(userName, commentId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in deleteing comment: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{commentId}/likes")
    public ResponseEntity<List<String>> likedByUsers(@PathVariable String commentId){
        try{
            List<String> list = commentService.likedByUsers(commentId);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in getting Likes: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<String> likeComment(@PathVariable Long commentId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try{
            boolean liked = commentService.likeComment(userName, commentId);
            if(liked) return new ResponseEntity<>("Liked the Comment", HttpStatus.CREATED);
            return new ResponseEntity<>("DisLiked the Comment", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in Liking Comment: ", e);
            return new ResponseEntity<>("Error in liking the Comment", HttpStatus.NOT_FOUND);
        }
    }
}


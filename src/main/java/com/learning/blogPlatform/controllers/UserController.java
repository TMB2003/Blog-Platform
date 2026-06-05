package com.learning.blogPlatform.controllers;

import com.learning.blogPlatform.entities.User;
import com.learning.blogPlatform.services.UserService;
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
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<User> getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User user = userService.findUser(userName);
        if(user == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody User user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try{
            User updatedUser = userService.updateUser(userName, user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            log.error("User not updated: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try{
            userService.deleteUser(userName);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Error in deleting User: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/followers")
    public ResponseEntity<List<String>> getFollowers(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try{
            List<String> list = userService.getFollowers(userName);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Followers Not Found: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/followings")
    public ResponseEntity<List<String>> getFollowing(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        try{
            List<String> list = userService.getFollowings(userName);
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Following Not Found: ", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/follow/{followingUserName}")
    public ResponseEntity<String> followUser(@PathVariable String followingUserName){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String followerUserName = authentication.getName();

        try{
            boolean result = userService.followUser(followerUserName, followingUserName);

            if (result) return new ResponseEntity<>("Followed", HttpStatus.CREATED);
            else return new ResponseEntity<>("Unfollowed", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error in following: ", e);
            return new ResponseEntity<>("Error in following", HttpStatus.BAD_REQUEST);
        }
    }
}

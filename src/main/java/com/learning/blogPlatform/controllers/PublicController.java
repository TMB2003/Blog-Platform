package com.learning.blogPlatform.controllers;

import com.learning.blogPlatform.entities.User;
import com.learning.blogPlatform.services.UserDetailsServiceImpl;
import com.learning.blogPlatform.services.UserService;
import com.learning.blogPlatform.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @GetMapping("/health-check")
    public String healthCheck(){return "OK"; }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user){
        try{
            userService.createUser(user);
            String token = jwtUtils.generateToken(user.getUserName());
            return new ResponseEntity<>(token, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error in registering User: ", e);
            return new ResponseEntity<>("User not Registered", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user){
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            String token = jwtUtils.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in Login: ", e);
            return new ResponseEntity<>("User not Found" ,HttpStatus.BAD_REQUEST);
        }
    }
}

package com.learning.blogPlatform.services;

import com.learning.blogPlatform.entities.Follow;
import com.learning.blogPlatform.entities.User;
import com.learning.blogPlatform.repositories.FollowRepository;
import com.learning.blogPlatform.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FollowRepository followRepository;

    public void createUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public User updateUser(String name, User newUser){
        User oldUser = userRepository.findByUserName(name);

        if(oldUser == null) return null;

        if(newUser.getEmail() != null) oldUser.setEmail(newUser.getEmail());
        if(newUser.getPassword() != null) oldUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        if(newUser.getRole() != null) oldUser.setRole(newUser.getRole());

        return saveUser(oldUser);
    }

    public User findUser(String userName){
        return userRepository.findByUserName(userName);
    }

    public void deleteUser(String name){
        userRepository.deleteByUserName(name);
    }



    public List<String> getFollowers(String userName) {
        List<Follow> follows = followRepository.findByFollowingUserName(userName);

        return follows.stream()
                .map(Follow::getFollowerUserName)
                .toList();
    }

    public List<String> getFollowings(String userName){
        List<Follow> follows = followRepository.findByFollowerUserName(userName);

        return follows.stream()
                .map(Follow::getFollowingUserName)
                .toList();
    }

    @Transactional
    public boolean followUser(String followerUserName, String followingUserName) {

        if (followerUserName.equals(followingUserName)) {
            throw new IllegalArgumentException("User cannot follow themselves");
        }

        User follower = findUser(followerUserName);
        User following = findUser(followingUserName);

        boolean exists = followRepository
                .existsByFollowerUserNameAndFollowingUserName(
                        followerUserName,
                        followingUserName);

        if (exists) {

            followRepository.deleteByFollowerUserNameAndFollowingUserName(
                    followerUserName,
                    followingUserName);

            follower.setFollowing(follower.getFollowing() - 1);
            following.setFollower(following.getFollower() - 1);

            saveUser(follower);
            saveUser(following);

            return false;
        }

        Follow follow = new Follow();
        follow.setFollowerUserName(followerUserName);
        follow.setFollowingUserName(followingUserName);

        followRepository.save(follow);

        follower.setFollowing(follower.getFollowing() + 1);
        following.setFollower(following.getFollower() + 1);

        saveUser(follower);
        saveUser(following);

        return true;
    }
}

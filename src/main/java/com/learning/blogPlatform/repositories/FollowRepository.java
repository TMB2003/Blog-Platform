package com.learning.blogPlatform.repositories;

import com.learning.blogPlatform.entities.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFollowerUserName(String followerUserName);

    List<Follow> findByFollowingUserName(String followingUserName);

    boolean existsByFollowerUserNameAndFollowingUserName(
            String followerUserName,
            String followingUserName);

    void deleteByFollowerUserNameAndFollowingUserName(
            String followerUserName,
            String followingUserName);
}

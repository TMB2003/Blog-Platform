package com.learning.blogPlatform.repositories;

import com.learning.blogPlatform.entities.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("SELECT l.userName FROM Like l WHERE l.postId = :postId")
    List<String> findUserNamesByPostId(@Param("postId") String postId);

//    List<Like> findByUserName(String userName);

    boolean existsByPostIdAndUserName(String postId, String userName);

    void deleteByPostIdAndUserName(String postId, String userName);
}

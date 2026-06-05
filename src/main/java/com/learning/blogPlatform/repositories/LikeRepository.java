package com.learning.blogPlatform.repositories;

import com.learning.blogPlatform.entities.Like;
import com.learning.blogPlatform.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("""
           SELECT l.userName
           FROM Like l
           WHERE l.targetType = :targetType
           AND l.targetId = :targetId
           """)
    List<String> findUserNamesByPostId(
            @Param("targetType") TargetType targetType,
            @Param("targetId") String targetId
    );

    boolean existsByTargetIdAndTargetTypeAndUserName(
            String targetId,
            TargetType targetType,
            String userName
    );

    void deleteByTargetIdAndTargetTypeAndUserName(
            String targetId,
            TargetType targetType,
            String userName
    );
}

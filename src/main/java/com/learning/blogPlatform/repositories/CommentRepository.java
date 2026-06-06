package com.learning.blogPlatform.repositories;

import com.learning.blogPlatform.entities.Comment;
import com.learning.blogPlatform.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllBytargetIdAndTargetType(String targetId, TargetType targetType);
}

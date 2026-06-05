package com.learning.blogPlatform.repositories;

import com.learning.blogPlatform.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, String> {
}

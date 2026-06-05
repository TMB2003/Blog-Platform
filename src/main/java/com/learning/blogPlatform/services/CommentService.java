package com.learning.blogPlatform.services;

import com.learning.blogPlatform.entities.Comment;
import com.learning.blogPlatform.entities.Like;
import com.learning.blogPlatform.enums.TargetType;
import com.learning.blogPlatform.repositories.CommentRepository;
import com.learning.blogPlatform.repositories.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    public Comment createComment(String userName, String targetId, Comment comment){
        comment.setUserName(userName);
        comment.setTargetId(targetId);
        return commentRepository.save(comment);
    }

    public Comment saveComment(Comment comment){
        return commentRepository.save(comment);
    }

    public Comment findComment(String id){
        return commentRepository.findById(id).orElse(null);
    }

    public Comment getComment(String userName, String id){
        return findComment(id);
    }

    public Comment updateComment(String userName, String id, Comment newComment){
        Comment oldComment = findComment(id);
        if(oldComment == null) return null;

        if(!oldComment.getUserName().equals(userName)) return null;

        if(newComment.getContent() != null) oldComment.setContent(newComment.getContent());

        return saveComment(oldComment);
    }

    public void deleteComment(String userName, String id){
        Comment comment = findComment(id);
        if(!comment.getUserName().equals(userName)) return;

        commentRepository.deleteById(id);
    }

    public List<String> likedByUsers(String postId) {
        return likeRepository.findUserNamesByPostId(TargetType.COMMENT, postId);
    }

//    public List<Post> likedOnPosts(String userName) {
//        List<Like> likes = likeRepository.findByUserName(userName);
//
//        List<Post> list = new ArrayList<>();
//        for (Like like : likes) {
//            String postId = like.getPostId();
//            Post post = findPost(postId);
//            list.add(post);
//        }
//        return list;
//    }

    @Transactional
    public boolean likeComment(String userName, String commentId){
        Comment comment = findComment(commentId);
        if(comment == null) {
            throw new IllegalArgumentException("Post not found");
        }
        boolean exist = likeRepository.existsByTargetIdAndTargetTypeAndUserName(
                commentId, TargetType.COMMENT,userName);

        if(exist){
            comment.setLikeCount(comment.getLikeCount() - 1);
            likeRepository.deleteByTargetIdAndTargetTypeAndUserName(commentId, TargetType.COMMENT, userName);
            saveComment(comment);
            return false;
        }

        Like like = new Like();
        like.setTargetId(commentId);
        like.setTargetType(TargetType.COMMENT);
        like.setUserName(userName);

        comment.setLikeCount(comment.getLikeCount() + 1);
        saveComment(comment);
        likeRepository.save(like);
        return true;
    }
}

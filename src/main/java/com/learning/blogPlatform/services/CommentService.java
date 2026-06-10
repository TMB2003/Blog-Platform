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

    public List<Comment> getAll(String targetId, TargetType targetType){
        return commentRepository.findAllBytargetIdAndTargetType(targetId, targetType);
    }

    public Comment saveComment(Comment comment){
        return commentRepository.save(comment);
    }

    public Comment findComment(Long id){
        return commentRepository.findById(id).orElse(null);
    }

    public Comment updateComment(String userName, Long id, Comment newComment){
        Comment oldComment = findComment(id);
        if(oldComment == null) return null;

        if(!oldComment.getUserName().equals(userName)) return null;

        if(newComment.getContent() != null) oldComment.setContent(newComment.getContent());

        return saveComment(oldComment);
    }

    public void deleteComment(String userName, Long id){
        Comment comment = findComment(id);
        if(comment == null) return;
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
    public boolean likeComment(String userName, Long commentId){
        Comment comment = findComment(commentId);
        if(comment == null) {
            throw new IllegalArgumentException("Post not found");
        }
        boolean exist = likeRepository.existsByTargetIdAndTargetTypeAndUserName(
                commentId.toString(), TargetType.COMMENT,userName);

        if(exist){
            comment.setLikeCount(comment.getLikeCount() - 1);
            likeRepository.deleteByTargetIdAndTargetTypeAndUserName(commentId.toString(), TargetType.COMMENT, userName);
            saveComment(comment);
            return false;
        }

        Like like = new Like();
        like.setTargetId(commentId.toString());
        like.setTargetType(TargetType.COMMENT);
        like.setUserName(userName);

        comment.setLikeCount(comment.getLikeCount() + 1);
        saveComment(comment);
        likeRepository.save(like);
        return true;
    }
}

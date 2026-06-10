package com.learning.blogPlatform.ServiceTest;

import com.learning.blogPlatform.entities.Comment;
import com.learning.blogPlatform.entities.Like;
import com.learning.blogPlatform.enums.TargetType;
import com.learning.blogPlatform.repositories.CommentRepository;
import com.learning.blogPlatform.repositories.LikeRepository;
import com.learning.blogPlatform.services.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    @Test
    void test_createComment() {

        Comment comment = new Comment();
        comment.setTargetType(TargetType.POST);
        comment.setId(1L);

        when(commentRepository.save(comment)).thenReturn(comment);

        Comment savedComment =
                commentService.createComment("test", "12", comment);

        verify(commentRepository).save(comment);

        assertEquals(1L, savedComment.getId());
        assertEquals(TargetType.POST, savedComment.getTargetType());
        assertEquals("test", savedComment.getUserName());
        assertEquals("12", savedComment.getTargetId());
    }

    @Test
    void test_getAll() {

        List<Comment> comments = List.of(
                new Comment(),
                new Comment()
        );

        when(commentRepository.findAllBytargetIdAndTargetType(
                "1",
                TargetType.POST
        )).thenReturn(comments);

        List<Comment> result =
                commentService.getAll("1", TargetType.POST);

        assertEquals(2, result.size());

        verify(commentRepository)
                .findAllBytargetIdAndTargetType(
                        "1",
                        TargetType.POST
                );
    }

    @Test
    void test_findComment() {

        Comment comment = new Comment();
        comment.setId(1L);

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        Comment result = commentService.findComment(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void test_updateComment() {

        Comment oldComment = new Comment();
        oldComment.setId(1L);
        oldComment.setUserName("test");
        oldComment.setContent("Old Comment");

        Comment newComment = new Comment();
        newComment.setContent("New Comment Testing");

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(oldComment));

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Comment comment =
                commentService.updateComment(
                        "test",
                        1L,
                        newComment
                );

        ArgumentCaptor<Comment> captor =
                ArgumentCaptor.forClass(Comment.class);

        verify(commentRepository).save(captor.capture());

        assertEquals(
                "New Comment Testing",
                captor.getValue().getContent()
        );

        assertEquals(
                "New Comment Testing",
                comment.getContent()
        );
    }

    @Test
    void test_updateComment_NullContent() {

        Comment oldComment = new Comment();
        oldComment.setId(1L);
        oldComment.setUserName("test");
        oldComment.setContent("Old Comment");

        Comment newComment = new Comment();
        newComment.setContent(null);

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(oldComment));

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Comment result =
                commentService.updateComment(
                        "test",
                        1L,
                        newComment
                );

        assertEquals("Old Comment", result.getContent());
    }

    @Test
    void test_updateComment_OldCommentNull() {

        Comment newComment = new Comment();
        newComment.setContent("New Comment Testing");

        when(commentRepository.findById(1L))
                .thenReturn(Optional.empty());

        Comment comment =
                commentService.updateComment(
                        "test",
                        1L,
                        newComment
                );

        verify(commentRepository).findById(1L);
        verify(commentRepository, never()).save(any());

        assertNull(comment);
    }

    @Test
    void test_updateComment_UserNameNotEqual() {

        Comment oldComment = new Comment();
        oldComment.setId(1L);
        oldComment.setUserName("test1");

        Comment newComment = new Comment();
        newComment.setContent("New Comment Testing");

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(oldComment));

        Comment comment =
                commentService.updateComment(
                        "test",
                        1L,
                        newComment
                );

        verify(commentRepository).findById(1L);
        verify(commentRepository, never()).save(any());

        assertNull(comment);
    }

    @Test
    void test_deleteComment() {

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setUserName("test");

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        commentService.deleteComment("test", 1L);

        verify(commentRepository).findById(1L);
        verify(commentRepository).deleteById(1L);
    }

    @Test
    void test_deleteComment_CommentIsNull() {

        when(commentRepository.findById(1L))
                .thenReturn(Optional.empty());

        commentService.deleteComment("test", 1L);

        verify(commentRepository).findById(1L);
        verify(commentRepository, never()).deleteById(anyLong());
    }

    @Test
    void test_deleteComment_UserNameNotEqual() {

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setUserName("test1");

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        commentService.deleteComment("test", 1L);

        verify(commentRepository).findById(1L);
        verify(commentRepository, never()).deleteById(anyLong());
    }

    @Test
    void test_likedByUsers() {

        when(likeRepository.findUserNamesByPostId(
                TargetType.COMMENT,
                "1"))
                .thenReturn(List.of("john", "alice"));

        List<String> result =
                commentService.likedByUsers("1");

        assertEquals(2, result.size());
        assertTrue(result.contains("john"));
        assertTrue(result.contains("alice"));
    }

    @Test
    void test_likeComment_CommentIsNull() {

        when(commentRepository.findById(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> commentService.likeComment("test", 1L)
                );

        assertEquals(
                "Post not found",
                exception.getMessage()
        );
    }

    @Test
    void test_likeComment_AlreadyLiked() {

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setUserName("test");
        comment.setLikeCount(5L);

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        when(likeRepository.existsByTargetIdAndTargetTypeAndUserName(
                "1",
                TargetType.COMMENT,
                "test"
        )).thenReturn(true);

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        boolean result =
                commentService.likeComment(
                        "test",
                        1L
                );

        assertFalse(result);
        assertEquals(4L, comment.getLikeCount());

        verify(likeRepository)
                .deleteByTargetIdAndTargetTypeAndUserName(
                        "1",
                        TargetType.COMMENT,
                        "test"
                );

        verify(commentRepository).save(comment);
        verify(likeRepository, never()).save(any());
    }

    @Test
    void test_likeComment_NewLike() {

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setUserName("test");
        comment.setLikeCount(5L);

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        when(likeRepository.existsByTargetIdAndTargetTypeAndUserName(
                "1",
                TargetType.COMMENT,
                "test"
        )).thenReturn(false);

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        boolean result =
                commentService.likeComment(
                        "test",
                        1L
                );

        assertTrue(result);
        assertEquals(6L, comment.getLikeCount());

        ArgumentCaptor<Like> likeCaptor =
                ArgumentCaptor.forClass(Like.class);

        verify(likeRepository).save(likeCaptor.capture());

        Like savedLike = likeCaptor.getValue();

        assertEquals("1", savedLike.getTargetId());
        assertEquals(TargetType.COMMENT, savedLike.getTargetType());
        assertEquals("test", savedLike.getUserName());

        verify(likeRepository, never())
                .deleteByTargetIdAndTargetTypeAndUserName(
                        anyString(),
                        any(),
                        anyString()
                );
    }
}

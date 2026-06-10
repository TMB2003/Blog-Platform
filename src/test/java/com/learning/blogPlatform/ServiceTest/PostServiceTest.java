package com.learning.blogPlatform.ServiceTest;

import com.learning.blogPlatform.entities.Like;
import com.learning.blogPlatform.entities.NotificationEvent;
import com.learning.blogPlatform.entities.Post;
import com.learning.blogPlatform.entities.User;
import com.learning.blogPlatform.enums.TargetType;
import com.learning.blogPlatform.repositories.LikeRepository;
import com.learning.blogPlatform.repositories.PostRepository;
import com.learning.blogPlatform.repositories.UserRepository;
import com.learning.blogPlatform.services.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private RedisService redisService;

    @Mock
    private LikeRepository likeRepository;

    @Test
    public void test_createPost_WithImage() throws IOException {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "test".getBytes()
        );

        User user = new User();
        user.setUserName("test");
        user.setEmail("test@gmail.com");

        Post savedPost = new Post();
        savedPost.setId("1");
        savedPost.setUserName("test");
        savedPost.setCaption("caption");
        savedPost.setImageUrl("https://cloudinary.com/test.jpg");

        when(cloudinaryService.uploadImage(file)).thenReturn("https://cloudinary.com/test.jpg");
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        when(userRepository.findByUserName("test")).thenReturn(user);

        Post result = postService.createPost("test", "caption", file);

        assertEquals("1", result.getId());

        verify(cloudinaryService).uploadImage(file);
        verify(postRepository).save(any(Post.class));
        verify(redisService).savePost(savedPost);
        verify(kafkaProducerService).sendPostNotification(any(NotificationEvent.class));
    }

    @Test
    public void test_createPost_ImageIsNull() throws IOException {
        String userName = "john";
        String caption = "Post without image";

        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(true);

        Post savedPost = new Post();
        savedPost.setUserName(userName);
        savedPost.setCaption(caption);

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        User user = new User();
        user.setUserName(userName);
        user.setEmail("john@example.com");

        when(userRepository.findByUserName(userName))
                .thenReturn(user);

        Post result = postService.createPost(userName, caption, file);

        assertNotNull(result);
        assertNull(result.getImageUrl());

        verify(cloudinaryService, never()).uploadImage(any());
        verify(postRepository).save(any(Post.class));
        verify(redisService).savePost(savedPost);
        verify(kafkaProducerService).sendPostNotification(any(NotificationEvent.class));
    }

    @Test
    void findPost_ShouldReturnFromRedis() {

        Post post = new Post();
        post.setId("1");

        when(redisService.getPost("1")).thenReturn(post);

        Post result = postService.findPost("1");

        assertNotNull(result);
        verify(postRepository, never()).findById(anyString());
    }

    @Test
    void findPost_ShouldReturnFromDatabaseWhenRedisMiss() {

        Post post = new Post();
        post.setId("1");

        when(redisService.getPost("1")).thenReturn(null);
        when(postRepository.findById("1"))
                .thenReturn(Optional.of(post));

        Post result = postService.findPost("1");

        assertNotNull(result);
        verify(redisService).savePost(post);
    }

    @Test
    void updatePost_ShouldUpdateCaptionAndImage() throws IOException {

        MultipartFile file = mock(MultipartFile.class);

        Post oldPost = new Post();
        oldPost.setId("1");
        oldPost.setUserName("john");
        oldPost.setCaption("old");
        oldPost.setImageUrl("old-url");

        when(redisService.getPost("1")).thenReturn(oldPost);

        when(file.isEmpty()).thenReturn(false);

        PostService spyService = spy(postService);

        doReturn("new-url")
                .when(spyService)
                .uploadImage(file);

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Post result =
                spyService.updatePost("john", "1", "new caption", file);

        assertEquals("new caption", result.getCaption());
        assertEquals("new-url", result.getImageUrl());
    }

    @Test
    void updatePost_ShouldReturnNull_WhenPostNotFound() throws IOException {

        when(redisService.getPost("1")).thenReturn(null);
        when(postRepository.findById("1")).thenReturn(Optional.empty());

        Post result =
                postService.updatePost("john", "1", "new caption", null);

        assertNull(result);

        verify(postRepository, never()).save(any());
    }

    @Test
    void updatePost_ShouldReturnNull_WhenUserIsNotOwner() throws IOException {

        Post oldPost = new Post();
        oldPost.setId("1");
        oldPost.setUserName("owner");

        when(redisService.getPost("1")).thenReturn(oldPost);

        Post result =
                postService.updatePost("john", "1", "new caption", null);

        assertNull(result);

        verify(postRepository, never()).save(any());
    }

    @Test
    void updatePost_ShouldUpdateCaptionOnly() throws IOException {

        Post oldPost = new Post();
        oldPost.setId("1");
        oldPost.setUserName("john");
        oldPost.setCaption("old caption");
        oldPost.setImageUrl("old-url");

        when(redisService.getPost("1")).thenReturn(oldPost);

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Post result =
                postService.updatePost("john", "1", "new caption", null);

        assertNotNull(result);
        assertEquals("new caption", result.getCaption());
        assertEquals("old-url", result.getImageUrl());
    }

    @Test
    void updatePost_ShouldUpdateImageOnly() throws IOException {

        MultipartFile file = mock(MultipartFile.class);

        Post oldPost = new Post();
        oldPost.setId("1");
        oldPost.setUserName("john");
        oldPost.setCaption("old caption");
        oldPost.setImageUrl("old-url");

        when(redisService.getPost("1")).thenReturn(oldPost);

        when(file.isEmpty()).thenReturn(false);

        PostService spyService = spy(postService);

        doReturn("new-image-url")
                .when(spyService)
                .uploadImage(file);

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Post result =
                spyService.updatePost("john", "1", null, file);

        assertEquals("old caption", result.getCaption());
        assertEquals("new-image-url", result.getImageUrl());
    }

    @Test
    void updatePost_ShouldKeepExistingValues_WhenCaptionAndFileAreNull()
            throws IOException {

        Post oldPost = new Post();
        oldPost.setId("1");
        oldPost.setUserName("john");
        oldPost.setCaption("old caption");
        oldPost.setImageUrl("old-url");

        when(redisService.getPost("1")).thenReturn(oldPost);

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Post result =
                postService.updatePost("john", "1", null, null);

        assertEquals("old caption", result.getCaption());
        assertEquals("old-url", result.getImageUrl());
    }

    @Test
    void updatePost_ShouldIgnoreEmptyCaptionAndEmptyFile()
            throws IOException {

        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(true);

        Post oldPost = new Post();
        oldPost.setId("1");
        oldPost.setUserName("john");
        oldPost.setCaption("old caption");
        oldPost.setImageUrl("old-url");

        when(redisService.getPost("1")).thenReturn(oldPost);

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Post result =
                postService.updatePost("john", "1", "", file);

        assertEquals("old caption", result.getCaption());
        assertEquals("old-url", result.getImageUrl());
    }

    @Test
    void likePost_ShouldLikePostSuccessfully() {

        Post post = new Post();
        post.setId("1");
        post.setUserName("owner");
        post.setCaption("caption");
        post.setLikeCount(0L);

        User owner = new User();
        owner.setEmail("owner@test.com");

        when(redisService.getPost("1")).thenReturn(post);

        when(likeRepository.existsByTargetIdAndTargetTypeAndUserName(
                "1",
                TargetType.POST,
                "john"))
                .thenReturn(false);

        when(userRepository.findByUserName("owner"))
                .thenReturn(owner);

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = postService.likePost("john", "1");

        assertTrue(result);
        assertEquals(1, post.getLikeCount());

        verify(likeRepository).save(any(Like.class));
        verify(kafkaProducerService)
                .sendLikeNotification(any(NotificationEvent.class));
    }

    @Test
    void likePost_ShouldUnlikePost_WhenAlreadyLiked() {

        Post post = new Post();
        post.setId("1");
        post.setLikeCount(5L);

        when(redisService.getPost("1")).thenReturn(post);

        when(likeRepository.existsByTargetIdAndTargetTypeAndUserName(
                "1",
                TargetType.POST,
                "john"))
                .thenReturn(true);

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = postService.likePost("john", "1");

        assertFalse(result);
        assertEquals(4, post.getLikeCount());

        verify(likeRepository)
                .deleteByTargetIdAndTargetTypeAndUserName(
                        "1",
                        TargetType.POST,
                        "john"
                );

        verify(likeRepository, never()).save(any());
    }

    @Test
    void likePost_ShouldThrowException_WhenPostNotFound() {

        when(redisService.getPost("1")).thenReturn(null);
        when(postRepository.findById("1"))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> postService.likePost("john", "1")
                );

        assertEquals("Post not found", ex.getMessage());
    }

    @Test
    void likedByUsers_ShouldReturnUserNames() {

        when(likeRepository.findUserNamesByPostId(
                TargetType.POST,
                "1"))
                .thenReturn(java.util.List.of("john", "alice"));

        assertEquals(
                2,
                postService.likedByUsers("1").size()
        );
    }
}
package com.learning.blogPlatform.ServiceTest;

import com.learning.blogPlatform.entities.Follow;
import com.learning.blogPlatform.entities.User;
import com.learning.blogPlatform.enums.Role;
import com.learning.blogPlatform.repositories.FollowRepository;
import com.learning.blogPlatform.repositories.UserRepository;
import com.learning.blogPlatform.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private FollowRepository followRepository;

    @Test
    void test_createUser() {

        User user = new User();
        user.setUserName("test");
        user.setPassword("password");

        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        userService.createUser(user);

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("encodedPassword", savedUser.getPassword());
    }

    @Test
    void test_saveUser() {

        User user = new User();
        user.setUserName("test");

        when(userRepository.save(user))
                .thenReturn(user);

        User result = userService.saveUser(user);

        assertEquals("test", result.getUserName());
        verify(userRepository).save(user);
    }

    @Test
    void test_findUser() {

        User user = new User();
        user.setUserName("test");

        when(userRepository.findByUserName("test"))
                .thenReturn(user);

        User result = userService.findUser("test");

        assertNotNull(result);
        assertEquals("test", result.getUserName());
    }

    @Test
    void test_deleteUser() {

        userService.deleteUser("test");

        verify(userRepository)
                .deleteByUserName("test");
    }

    @Test
    void test_updateUser_AllFields() {

        User oldUser = new User();
        oldUser.setUserName("test");
        oldUser.setEmail("old@test.com");
        oldUser.setPassword("oldPass");

        User newUser = new User();
        newUser.setEmail("new@test.com");
        newUser.setPassword("newPass");
        newUser.setRole(Role.ADMIN);

        when(userRepository.findByUserName("test"))
                .thenReturn(oldUser);

        when(passwordEncoder.encode("newPass"))
                .thenReturn("encodedPass");

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        User result =
                userService.updateUser("test", newUser);

        assertNotNull(result);
        assertEquals("new@test.com", result.getEmail());
        assertEquals("encodedPass", result.getPassword());
        assertEquals(Role.ADMIN, result.getRole());
    }

    @Test
    void test_updateUser_UserNotFound() {

        when(userRepository.findByUserName("test"))
                .thenReturn(null);

        User result =
                userService.updateUser("test", new User());

        assertNull(result);

        verify(userRepository, never())
                .save(any());
    }

    @Test
    void test_updateUser_EmailOnly() {

        User oldUser = new User();
        oldUser.setUserName("test");
        oldUser.setEmail("old@test.com");

        User newUser = new User();
        newUser.setEmail("new@test.com");

        when(userRepository.findByUserName("test"))
                .thenReturn(oldUser);

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        User result =
                userService.updateUser("test", newUser);

        assertEquals("new@test.com", result.getEmail());
    }

    @Test
    void test_updateUser_PasswordOnly() {

        User oldUser = new User();
        oldUser.setUserName("test");

        User newUser = new User();
        newUser.setPassword("newPass");

        when(userRepository.findByUserName("test"))
                .thenReturn(oldUser);

        when(passwordEncoder.encode("newPass"))
                .thenReturn("encodedPass");

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        User result =
                userService.updateUser("test", newUser);

        assertEquals("encodedPass", result.getPassword());
    }

    @Test
    void test_getFollowers() {

        Follow f1 = new Follow();
        f1.setFollowerUserName("john");

        Follow f2 = new Follow();
        f2.setFollowerUserName("alice");

        when(followRepository.findByFollowingUserName("test"))
                .thenReturn(List.of(f1, f2));

        List<String> result =
                userService.getFollowers("test");

        assertEquals(2, result.size());
        assertTrue(result.contains("john"));
        assertTrue(result.contains("alice"));
    }

    @Test
    void test_getFollowings() {

        Follow f1 = new Follow();
        f1.setFollowingUserName("john");

        Follow f2 = new Follow();
        f2.setFollowingUserName("alice");

        when(followRepository.findByFollowerUserName("test"))
                .thenReturn(List.of(f1, f2));

        List<String> result =
                userService.getFollowings("test");

        assertEquals(2, result.size());
        assertTrue(result.contains("john"));
        assertTrue(result.contains("alice"));
    }

    @Test
    void test_followUser_SelfFollow() {

        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.followUser(
                                "john",
                                "john")
                );

        assertEquals(
                "User cannot follow themselves",
                ex.getMessage()
        );
    }

    @Test
    void test_followUser_NewFollow() {

        User follower = new User();
        follower.setUserName("john");
        follower.setFollowing(5L);

        User following = new User();
        following.setUserName("alice");
        following.setFollower(10L);

        when(userRepository.findByUserName("john"))
                .thenReturn(follower);

        when(userRepository.findByUserName("alice"))
                .thenReturn(following);

        when(followRepository
                .existsByFollowerUserNameAndFollowingUserName(
                        "john",
                        "alice"))
                .thenReturn(false);

        boolean result =
                userService.followUser(
                        "john",
                        "alice");

        assertTrue(result);

        verify(followRepository)
                .save(any(Follow.class));

        assertEquals(6L, follower.getFollowing());
        assertEquals(11L, following.getFollower());
    }

    @Test
    void test_followUser_Unfollow() {

        User follower = new User();
        follower.setUserName("john");
        follower.setFollowing(5L);

        User following = new User();
        following.setUserName("alice");
        following.setFollower(10L);

        when(userRepository.findByUserName("john"))
                .thenReturn(follower);

        when(userRepository.findByUserName("alice"))
                .thenReturn(following);

        when(followRepository
                .existsByFollowerUserNameAndFollowingUserName(
                        "john",
                        "alice"))
                .thenReturn(true);

        boolean result =
                userService.followUser(
                        "john",
                        "alice");

        assertFalse(result);

        verify(followRepository)
                .deleteByFollowerUserNameAndFollowingUserName(
                        "john",
                        "alice");

        assertEquals(4L, follower.getFollowing());
        assertEquals(9L, following.getFollower());
    }
}
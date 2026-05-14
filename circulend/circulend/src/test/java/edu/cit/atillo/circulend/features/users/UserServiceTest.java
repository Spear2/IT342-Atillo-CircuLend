package edu.cit.atillo.circulend.features.users;

import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.entity.enums.Role;
import edu.cit.atillo.circulend.features.auth.AuthException;
import edu.cit.atillo.circulend.repository.UserRepository;
import edu.cit.atillo.circulend.security.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private UserService userService;

    @Test
    void listUsersReturnsMappedUsers() {
        User user = new User();
        user.setUserId(1L);
        user.setEmail("a@b.com");
        user.setFirstName("A");
        user.setLastName("B");
        user.setRole(Role.BORROWER);
        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        var result = userService.listUsers(0, 500);
        assertEquals(1, result.size());
        assertEquals("a@b.com", result.get(0).getEmail());
    }

    @Test
    void getCurrentUserThrowsWhenNotFound() {
        when(currentUserService.getCurrentUserId()).thenReturn(123L);
        when(userRepository.findById(123L)).thenReturn(Optional.empty());

        AuthException ex = assertThrows(AuthException.class, userService::getCurrentUser);
        assertEquals("AUTH-002", ex.getCode());
    }
}

package edu.cit.atillo.circulend.features.users;

import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.features.auth.AuthException;
import edu.cit.atillo.circulend.features.users.dto.UserResponseDTO;
import edu.cit.atillo.circulend.repository.UserRepository;
import edu.cit.atillo.circulend.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public List<UserResponseDTO> listUsers(int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        return userRepository.findAll(PageRequest.of(page, safeSize))
                .map(UserResponseDTO::fromUser)
                .getContent();
    }

    public UserResponseDTO getCurrentUser() {
        Long userId = currentUserService.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(
                        "AUTH-002",
                        "Invalid or expired token",
                        HttpStatus.UNAUTHORIZED
                ));
        return UserResponseDTO.fromUser(user);
    }
}

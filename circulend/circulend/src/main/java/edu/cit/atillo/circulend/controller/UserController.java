package edu.cit.atillo.circulend.controller;

import edu.cit.atillo.circulend.dto.ApiResponse;
import edu.cit.atillo.circulend.dto.UserResponseDTO;
import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.exception.AuthException;
import edu.cit.atillo.circulend.repository.UserRepository;
import edu.cit.atillo.circulend.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class UserController {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    public UserController(CurrentUserService currentUserService, UserRepository userRepository) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDTO>> me() {
        Long userId = currentUserService.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(
                        "AUTH-002",
                        "Invalid or expired token",
                        HttpStatus.UNAUTHORIZED
                ));

        return ResponseEntity.ok(ApiResponse.success(UserResponseDTO.fromUser(user)));
    }
}
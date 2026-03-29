package edu.cit.atillo.circulend.service;

import edu.cit.atillo.circulend.dto.*;
import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.entity.enums.AuthProvider;
import edu.cit.atillo.circulend.entity.enums.Role;
import edu.cit.atillo.circulend.repository.UserRepository;
import edu.cit.atillo.circulend.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public AuthService(UserRepository userRepo,
                       PasswordEncoder passwordEncoder,
                       TokenProvider tokenProvider) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public LoginDataDTO login(LoginRequestDTO dto){
        User user = userRepo.findByEmail(dto.getEmail()).
                orElseThrow(() -> new RuntimeException("User not found"));
        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid Password!");
        }
        String token = tokenProvider.createToken(user.getUserId(), user.getRole().name());
        return new LoginDataDTO(UserResponseDTO.fromUser(user), token, null);
    }

    public User registration(RegisterDTO dto){
        if(userRepo.existsByEmail(dto.getEmail())){
            throw new RuntimeException("Email Already Exists!");
        }
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.BORROWER);
        user.setAuthProvider(AuthProvider.LOCAL);

        return userRepo.save(user);
    }

    public User getUserFromToken(String token) {
        Long userId = tokenProvider.getUserIdFromToken(token);
        return userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}

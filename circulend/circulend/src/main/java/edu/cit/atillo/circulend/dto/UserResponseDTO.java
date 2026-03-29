package edu.cit.atillo.circulend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("lastname")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    private String role;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    public static UserResponseDTO fromUser(edu.cit.atillo.circulend.entity.User user) {
        if (user == null) return null;
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
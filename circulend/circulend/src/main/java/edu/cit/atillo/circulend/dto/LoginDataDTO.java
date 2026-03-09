package edu.cit.atillo.circulend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDataDTO {
    private UserResponseDTO user;
    private String accessToken;
    private String refreshToken; // optional; can be null until you implement refresh
}
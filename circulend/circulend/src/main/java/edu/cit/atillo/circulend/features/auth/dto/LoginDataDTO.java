package edu.cit.atillo.circulend.features.auth.dto;

import edu.cit.atillo.circulend.features.users.dto.UserResponseDTO;
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
    private String refreshToken;
}

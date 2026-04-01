package edu.cit.atillo.circulend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoogleUserClaimsDTO {
    private String email;
    private String givenName;
    private String familyName;
    private String sub;
}
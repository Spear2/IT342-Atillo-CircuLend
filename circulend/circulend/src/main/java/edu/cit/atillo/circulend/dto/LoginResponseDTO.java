package edu.cit.atillo.circulend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponseDTO {
    private String token;
    private String email;

    public LoginResponseDTO(String token, String email){
        this.token = token;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken(){
        return token;
    }
    public void setToken(String token){
        this.token=token;
    }
}

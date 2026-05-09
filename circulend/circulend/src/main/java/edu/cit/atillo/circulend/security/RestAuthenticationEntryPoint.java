package edu.cit.atillo.circulend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cit.atillo.circulend.features.shared.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        String msg = (String) request.getAttribute("auth_error_message");
        if (msg == null || msg.isBlank()) msg = "Authentication required or token invalid";

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> body = ApiResponse.failure("AUTH-002", msg, null);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
package edu.cit.atillo.circulend.security;

import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.entity.enums.AuthProvider;
import edu.cit.atillo.circulend.entity.enums.Role;
import edu.cit.atillo.circulend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepo;
    private final TokenProvider tokenProvider;

    @Value("${app.frontend.oauth2-success-url}")
    private String successUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = (String) oAuth2User.getAttributes().get("email");
        String sub = (String) oAuth2User.getAttributes().get("sub");
        String firstName = cleanName((String) oAuth2User.getAttributes().getOrDefault("given_name", "Google"));
        String lastName = cleanName((String) oAuth2User.getAttributes().getOrDefault("family_name", "User"));

        System.out.println("given_name: " + oAuth2User.getAttributes().get("given_name"));
        System.out.println("family_name: " + oAuth2User.getAttributes().get("family_name"));

        User user = userRepo.findByGoogleSub(sub)
                .or(() -> userRepo.findByEmail(email))
                .orElseGet(User::new);

        if (user.getUserId() == null) {
            user.setEmail(email);
            user.setFirstName(lastName);
            user.setLastName(firstName);
            user.setRole(Role.BORROWER);
        }

        user.setAuthProvider(AuthProvider.GOOGLE);
        user.setGoogleSub(sub);
        user.setEmailVerified(true);
        user.setVerifiedAt(LocalDateTime.now());
        user.setPassword(null);

        User saved = userRepo.save(user);

        String jwt = tokenProvider.createToken(saved.getUserId(), saved.getRole().name());

        String redirect = UriComponentsBuilder.fromUriString(successUrl)
                .queryParam("token", jwt)
                .queryParam("role", saved.getRole().name())
                .build(true)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirect);
    }

    private String cleanName(String name) {
        if (name == null) return "";
        return name
                .replace(",", "")   // remove trailing commas
                .replaceAll("\\s+[A-Z]\\.$", "")  // remove middle initial like " C."
                .trim();
    }
}
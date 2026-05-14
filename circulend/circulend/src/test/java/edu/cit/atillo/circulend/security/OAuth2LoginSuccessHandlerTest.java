package edu.cit.atillo.circulend.security;

import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.entity.enums.AuthProvider;
import edu.cit.atillo.circulend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2LoginSuccessHandlerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private Authentication authentication;
    @Mock
    private OAuth2User oAuth2User;

    @Test
    void redirectsWithErrorWhenLocalAccountWithSameEmailExists() throws Exception {
        OAuth2LoginSuccessHandler handler = new OAuth2LoginSuccessHandler(userRepository, tokenProvider);
        ReflectionTestUtils.setField(handler, "successUrl", "http://localhost:5173/oauth2/success");

        User localUser = new User();
        localUser.setAuthProvider(AuthProvider.LOCAL);

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttributes()).thenReturn(Map.of(
                "email", "user@example.com",
                "sub", "google-sub",
                "given_name", "Ana",
                "family_name", "Atillo"
        ));
        when(userRepository.findByGoogleSub("google-sub")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(localUser));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication);

        assertTrue(response.getRedirectedUrl().contains("oauth2=email_exists_local"));
    }
}

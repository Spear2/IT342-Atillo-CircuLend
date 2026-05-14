package edu.cit.atillo.circulend.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityHandlersTest {

    @Test
    void authenticationEntryPointReturnsAuthCode() throws Exception {
        RestAuthenticationEntryPoint entryPoint = new RestAuthenticationEntryPoint();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("auth_error_message", "Invalid token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new BadCredentialsException("bad"));

        assertTrue(response.getContentAsString().contains("AUTH-002"));
    }

    @Test
    void accessDeniedHandlerReturnsForbiddenCode() throws Exception {
        RestAccessDeniedHandler handler = new RestAccessDeniedHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("forbidden"));

        assertTrue(response.getContentAsString().contains("AUTH-003"));
    }
}

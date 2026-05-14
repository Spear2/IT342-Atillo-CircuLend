package edu.cit.atillo.circulend.features.shared.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void successBuildsExpectedPayload() {
        ApiResponse<String> response = ApiResponse.success("ok");

        assertTrue(response.isSuccess());
        assertEquals("ok", response.getData());
        assertNull(response.getError());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void failureBuildsExpectedPayload() {
        ApiResponse<Void> response = ApiResponse.failure("AUTH-001", "Invalid credentials", null);

        assertFalse(response.isSuccess());
        assertNull(response.getData());
        assertNotNull(response.getError());
        assertEquals("AUTH-001", response.getError().getCode());
        assertEquals("Invalid credentials", response.getError().getMessage());
        assertNotNull(response.getTimestamp());
    }
}

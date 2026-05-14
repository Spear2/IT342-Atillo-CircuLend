package edu.cit.atillo.circulend.features.shared.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenHashUtilTest {

    private final TokenHashUtil tokenHashUtil = new TokenHashUtil();

    @Test
    void sha256IsDeterministicAndHexEncoded() {
        String hashA = tokenHashUtil.sha256("sample-token");
        String hashB = tokenHashUtil.sha256("sample-token");

        assertEquals(hashA, hashB);
        assertEquals(64, hashA.length());
        assertTrue(hashA.matches("[0-9a-f]+"));
    }

    @Test
    void sha256ProducesDifferentOutputForDifferentInputs() {
        String hashA = tokenHashUtil.sha256("sample-token");
        String hashB = tokenHashUtil.sha256("another-token");

        assertNotEquals(hashA, hashB);
    }
}

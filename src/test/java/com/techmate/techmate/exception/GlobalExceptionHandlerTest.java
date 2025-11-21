package com.techmate.techmate.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import com.techmate.techmate.dto.ApiErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GlobalExceptionHandlerTest {

    @Test
    void whenHandlerCalledDirectly_thenApiErrorResponseHasStructuredFields() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test/force-notfound");

        ResponseEntity<ApiErrorResponse> resp = handler.handleNotFound(new NotFoundException("forced not found"), request);

        assertEquals(404, resp.getStatusCode().value());
        ApiErrorResponse body = resp.getBody();
        assertNotNull(body);
        assertEquals(404, body.getStatus());
        assertEquals("/test/force-notfound", body.getPath());
        assertEquals("forced not found", body.getMessage());
        assertNotNull(body.getTimestamp());
    }
}


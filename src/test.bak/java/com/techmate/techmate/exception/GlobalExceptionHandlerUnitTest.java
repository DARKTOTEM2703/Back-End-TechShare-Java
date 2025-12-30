package com.techmate.techmate.exception;

import com.techmate.techmate.dto.ApiErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalExceptionHandlerUnitTest {

    @Test
    void handleNotFound_returnsApiErrorResponseWith404() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        req.setRequestURI("/test/not-found");

        NotFoundException ex = new NotFoundException("Recurso no encontrado");
        ResponseEntity<ApiErrorResponse> resp = handler.handleNotFound(ex, req);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatusCode().value()).isEqualTo(404);
        ApiErrorResponse body = resp.getBody();
        assertThat(body).isNotNull();
        if (body != null) {
            assertThat(body.getStatus()).isEqualTo(404);
            assertThat(body.getPath()).isEqualTo("/test/not-found");
            assertThat(body.getMessage()).isEqualTo("Recurso no encontrado");
        }
    }
}

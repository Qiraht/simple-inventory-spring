package com.dibimbing.apiassignment.exceptions.handler;

import com.dibimbing.apiassignment.exceptions.custom.NotFoundException;
import com.dibimbing.apiassignment.exceptions.custom.ValidationException;
import com.dibimbing.apiassignment.exceptions.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerUnitTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        ValidationException ex = new ValidationException("Invalid data");
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleBusinessException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("BAD_REQUEST", Objects.requireNonNull(response.getBody()).getError());
        assertEquals("Invalid data", response.getBody().getMessage());
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFound() {
        NotFoundException ex = new NotFoundException("Not found");
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleBusinessException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("NOT_FOUND", Objects.requireNonNull(response.getBody()).getError());
        assertEquals("Not found", response.getBody().getMessage());
    }

    @Test
    void handleGeneralException_ShouldReturnInternalError() {
        Exception ex = new Exception("Internal error");
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleBusinessException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_SERVER_ERROR", Objects.requireNonNull(response.getBody()).getError());
        assertEquals("Internal error", response.getBody().getMessage());
    }
}

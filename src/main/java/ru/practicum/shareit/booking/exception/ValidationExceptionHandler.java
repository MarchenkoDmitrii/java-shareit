package ru.practicum.shareit.booking.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException ex) {
        // Заменяем текст ошибки
        String error_message = "Unknown state: UNSUPPORTED_STATUS";

        CustomErrorResponse errorResponse = new CustomErrorResponse(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                500, error_message, "/bookings");

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Дополнительный класс для представления кастомного ответа об ошибке
    @Getter
    @Setter
    public static class CustomErrorResponse {
        private long timestamp;
        private int status;
        private String error;
        private String path;

        public CustomErrorResponse(long timestamp, int status, String error, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.path = path;
        }

    }
}

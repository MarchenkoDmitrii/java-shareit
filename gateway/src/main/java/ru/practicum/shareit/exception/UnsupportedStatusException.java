package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Unknown state: UNSUPPORTED_STATUS")
public class UnsupportedStatusException extends RuntimeException {
}

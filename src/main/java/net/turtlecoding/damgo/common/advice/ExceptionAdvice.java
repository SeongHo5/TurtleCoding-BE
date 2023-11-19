package net.turtlecoding.damgo.common.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.turtlecoding.damgo.common.exception.AuthException;
import net.turtlecoding.damgo.common.exception.dto.ExceptionDto;
import org.springframework.http.ResponseEntity;
import net.turtlecoding.damgo.common.exception.DuplicatedException;
import net.turtlecoding.damgo.common.exception.NotFoundException;
import net.turtlecoding.damgo.common.exception.ServiceFailedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvice {

    @ExceptionHandler({AuthException.class})
    protected ResponseEntity<ExceptionDto> authException(AuthException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.
                status(ex.getStatusCode()).
                body(new ExceptionDto(ex.getStatusCode(), ex.getMessage()));
    }

    @ExceptionHandler({DuplicatedException.class})
    protected ResponseEntity<ExceptionDto> duplicatedException(DuplicatedException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.
                status(ex.getStatusCode()).
                body(new ExceptionDto(ex.getStatusCode(), ex.getMessage()));
    }

    @ExceptionHandler({NotFoundException.class})
    protected ResponseEntity<ExceptionDto> notFoundException(NotFoundException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.
                status(ex.getStatusCode()).
                body(new ExceptionDto(ex.getStatusCode(), ex.getMessage()));
    }

    @ExceptionHandler({ServiceFailedException.class})
    protected ResponseEntity<ExceptionDto> serviceFailedException(ServiceFailedException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.
                status(ex.getStatusCode()).
                body(new ExceptionDto(ex.getStatusCode(), ex.getMessage()));
    }

    /**
     * Parameter Validation 실패 시
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<ExceptionDto> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.
                status(400).
                body(new ExceptionDto(400, "Constraint Failed"));
    }

    @ExceptionHandler({NullPointerException.class})
    protected ResponseEntity<ExceptionDto> nullPointerException(NullPointerException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.
                status(404).
                body(new ExceptionDto(404, "Not Found / Please Contact to Admin"));
    }
}

package net.turtlecoding.damgo.common.exception;

import lombok.Getter;
import net.turtlecoding.damgo.common.exception.enums.ExceptionStatus;

@Getter
public class DuplicatedException extends RuntimeException {

    private final int statusCode;
    private final String message;

    public DuplicatedException(ExceptionStatus status) {
        this.statusCode = status.getStatusCode();
        this.message = status.getMessage();
    }
}

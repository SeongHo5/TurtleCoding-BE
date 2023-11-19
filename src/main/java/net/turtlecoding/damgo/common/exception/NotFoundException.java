package net.turtlecoding.damgo.common.exception;

import lombok.Getter;
import net.turtlecoding.damgo.common.exception.enums.ExceptionStatus;
@Getter
public class NotFoundException extends RuntimeException {

    private final int statusCode;
    private final String message;

    public NotFoundException(ExceptionStatus status) {
        this.statusCode = status.getStatusCode();
        this.message = status.getMessage();
    }
}

package net.turtlecoding.damgo.common.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionDto {

    private final int statusCode;
    private final String message;

}

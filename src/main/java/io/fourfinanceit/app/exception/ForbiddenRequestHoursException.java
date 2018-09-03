package io.fourfinanceit.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
public class ForbiddenRequestHoursException extends RuntimeException {

    public ForbiddenRequestHoursException(long amount, int minHour, int maxHour) {
        super(String.format("Requests with maximum amount: '%s' on time: %s:00 - %s:59 are forbidden",
                amount, minHour, maxHour));
    }

}

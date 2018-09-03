package io.fourfinanceit.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class LoanRequestLimitsExceeded extends RuntimeException{

    public LoanRequestLimitsExceeded(String limit, Object value) {
        super(String.format("Loan limit: '%s' exceeded with value: '%s'", limit, value));
    }
}

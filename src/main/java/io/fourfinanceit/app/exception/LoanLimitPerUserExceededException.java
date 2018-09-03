package io.fourfinanceit.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
public class LoanLimitPerUserExceededException extends RuntimeException{

    public LoanLimitPerUserExceededException(int limit) {
        super(String.format("Loan limit per one user: '%s' exceeded", limit));
    }
}

package io.fourfinanceit.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
public class LoanNotApprovedException extends RuntimeException {

    public LoanNotApprovedException() {
        super("Refused, because loan is not approved");
    }
}

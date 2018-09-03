package io.fourfinanceit.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class LoanAmountReturnExceeds extends RuntimeException {

    public LoanAmountReturnExceeds(Double actual, Double required) {
        super(String.format("Requested amount to return: '%s' exceeds the required: '%s'", actual, required));
    }
}

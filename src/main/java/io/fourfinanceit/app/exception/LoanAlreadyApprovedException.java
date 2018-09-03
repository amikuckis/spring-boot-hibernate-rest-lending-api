package io.fourfinanceit.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class LoanAlreadyApprovedException extends RuntimeException{

    public LoanAlreadyApprovedException() {
        super("Loan is already approved");
    }

}

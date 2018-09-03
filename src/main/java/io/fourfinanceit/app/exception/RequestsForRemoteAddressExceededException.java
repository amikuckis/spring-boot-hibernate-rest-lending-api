package io.fourfinanceit.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
public class RequestsForRemoteAddressExceededException extends RuntimeException {

    public RequestsForRemoteAddressExceededException(String remoteAddress, int limit) {
        super(String.format("Requests for remote address: '%s' exceeded limit: '%s'", remoteAddress, limit));
    }
}

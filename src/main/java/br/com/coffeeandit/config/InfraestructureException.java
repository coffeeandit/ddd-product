package br.com.coffeeandit.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class InfraestructureException extends RuntimeException {

    public InfraestructureException(String message) {
        super(message);
    }
}

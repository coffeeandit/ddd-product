package br.com.coffeeandit.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class DomainBusinessException extends RuntimeException {
    public DomainBusinessException(String message) {
        super(message);
    }
}

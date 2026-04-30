package br.com.contadin.infrastructure.web.exception;

import org.springframework.http.HttpStatus;

public interface HttpStatusException {
    HttpStatus getStatus();
}

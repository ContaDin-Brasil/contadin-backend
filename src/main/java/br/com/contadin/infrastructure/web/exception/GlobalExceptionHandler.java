package br.com.contadin.infrastructure.web.exception;

import br.com.contadin.application.exception.ApplicationException;
import br.com.contadin.application.exception.usuario.EmailJaExistenteException;
import br.com.contadin.domain.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * DOMAIN EXCEPTIONS
     *
     * Usadas para violações de regras de negócio PURAS.
     * - Não dependem de banco
     * - Não dependem de fluxo
     * - Não dependem de caso de uso
     *
     * Exemplos:
     * - Email inválido
     * - Nome vazio
     * - Valor negativo
     *
     * Normalmente mapeadas para HTTP 400 (Bad Request).
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomainException(DomainException ex) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (ex instanceof HttpStatusException httpEx) {
            status = httpEx.getStatus();
        }

        ApiError error = new ApiError(
                status.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(error);
    }

    /**
     * APPLICATION EXCEPTIONS
     *
     * Usadas quando o FLUXO do caso de uso falha.
     * - Dependem de repositório
     * - Dependem de existência de dados
     * - Representam conflitos ou estados inválidos do uso do sistema
     *
     * Exemplos:
     * - Usuário não encontrado
     * - Categoria não existe
     * - Regra de fluxo quebrada
     *
     * Status padrão: 422 (Unprocessable Entity)
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiError> handleApplicationException(ApplicationException ex) {

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        if (ex instanceof HttpStatusException httpEx) {
            status = httpEx.getStatus();
        }

        ApiError error = new ApiError(
                status.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(error);
    }

    /**
     * EXCEÇÕES NÃO TRATADAS
     *
     * Qualquer erro inesperado:
     * - Bug
     * - NullPointer
     * - Erro de infraestrutura
     *
     * Nunca expõe detalhes ao cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Erro interno"
                ));
    }
}
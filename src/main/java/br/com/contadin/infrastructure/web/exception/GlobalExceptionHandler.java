package br.com.contadin.infrastructure.web.exception;

import br.com.contadin.application.exception.ApplicationException;
import br.com.contadin.domain.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * DOMAIN EXCEPTIONS
     * <p>
     * Usadas para violações de regras de negócio PURAS.
     * - Não dependem de banco
     * - Não dependem de fluxo
     * - Não dependem de caso de uso
     * <p>
     * Exemplos:
     * - Email inválido
     * - Nome vazio
     * - Valor negativo
     * <p>
     * Normalmente mapeadas para HTTP 400 (Bad Request).
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomainException(DomainException ex) {

        HttpStatus status = BAD_REQUEST;

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
     * <p>
     * Usadas quando o FLUXO do caso de uso falha.
     * - Dependem de repositório
     * - Dependem de existência de dados
     * - Representam conflitos ou estados inválidos do uso do sistema
     * <p>
     * Exemplos:
     * - Usuário não encontrado
     * - Categoria não existe
     * - Regra de fluxo quebrada
     * <p>
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
     * ERROS DE VALIDAÇÃO (Bean Validation)
     * <p>
     * Quando @Valid falha no RequestBody ou parâmetros.
     * Ex: @NotBlank, @Email, @SenhaForte, etc.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + normalizar(e.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        ApiError error = new ApiError(BAD_REQUEST.value(), mensagem, LocalDateTime.now());
        return ResponseEntity.status(BAD_REQUEST).body(error);
    }

    private String normalizar(String msg) {
        return msg == null ? "" : msg.replaceAll("\\s+", " ").trim();
    }

    /**
     * ERROS DE PARÂMETROS DE REQUISIÇÃO (Query/Path)
     * <p>
     * Quando há falha na conversão de parâmetros da URL.
     * Exemplos:
     * - Enum inválido em query param (ex: tipo=ABC)
     * - Número inválido (ex: _page=abc)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleBadRequest(MethodArgumentTypeMismatchException ex) {
        ApiError error = new ApiError(BAD_REQUEST.value(), "Parâmetro inválido na requisição.", LocalDateTime.now());
        return ResponseEntity.status(BAD_REQUEST).body(error);
    }

    /**
     * EXCEÇÕES NÃO TRATADAS
     * <p>
     * Qualquer erro inesperado:
     * - Bug
     * - NullPointer
     * - Erro de infraestrutura
     * <p>
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
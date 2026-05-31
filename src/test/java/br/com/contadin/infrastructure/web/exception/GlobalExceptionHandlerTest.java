package br.com.contadin.infrastructure.web.exception;

import br.com.contadin.application.exception.ApplicationException;
import br.com.contadin.application.exception.usuario.UsuarioNaoEncontradoException;
import br.com.contadin.domain.exception.DomainException;
import br.com.contadin.domain.exception.categoria.CategoriaInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
    }

    @Nested
    class DomainExceptions {

        @Test
        @DisplayName("Deve retornar 400 para DomainException sem HttpStatusException")
        void deveRetornar400ParaDomainException() {
            DomainException ex = new DomainException("Campo inválido");

            ResponseEntity<ApiError> response = handler.handleDomainException(ex);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().status());
            assertEquals("Campo inválido", response.getBody().message());
        }

        @Test
        @DisplayName("Deve usar status 400 para CategoriaInvalidaException (DomainException + HttpStatusException)")
        void deveUsarStatus400ParaCategoriaInvalidaException() {
            DomainException ex = new DomainException("Tipo inválido");

            ResponseEntity<ApiError> response = handler.handleDomainException(ex);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(400, response.getBody().status());
            assertEquals("Tipo inválido", response.getBody().message());
        }
    }

    @Nested
    class ApplicationExceptions {

        @Test
        @DisplayName("Deve retornar 422 para ApplicationException sem HttpStatusException")
        void deveRetornar422ParaApplicationException() {
            ApplicationException ex = new ApplicationException("Operação inválida") {
            };

            ResponseEntity<ApiError> response = handler.handleApplicationException(ex);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(422, response.getBody().status());
            assertEquals("Operação inválida", response.getBody().message());
        }

        @Test
        @DisplayName("Deve usar status customizado quando ApplicationException implementar HttpStatusException")
        void deveUsarStatusCustomizadoParaApplicationException() {
            UsuarioNaoEncontradoException ex = new UsuarioNaoEncontradoException("Usuário não encontrado");

            ResponseEntity<ApiError> response = handler.handleApplicationException(ex);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals(404, response.getBody().status());
            assertEquals("Usuário não encontrado", response.getBody().message());
        }
    }

    @Nested
    class ValidationExceptions {

        @Test
        @DisplayName("Deve retornar 400 com mensagem de validação formatada")
        void deveRetornar400ComMensagemValidacao() {
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);

            FieldError fieldError = new FieldError("obj", "email", "E-mail inválido");

            when(ex.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

            ResponseEntity<ApiError> response = handler.handleValidation(ex);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().status());
            assertEquals("email: E-mail inválido", response.getBody().message());
        }

        @Test
        @DisplayName("Deve concatenar múltiplos erros de validação separados por ponto e vírgula")
        void deveConcatenarMultiplosErros() {
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);

            List<FieldError> errors = List.of(
                    new FieldError("obj", "nome", "Nome é obrigatório"),
                    new FieldError("obj", "email", "E-mail inválido")
            );

            when(ex.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(errors);

            ResponseEntity<ApiError> response = handler.handleValidation(ex);

            String message = response.getBody().message();
            assertNotNull(message);
            assertEquals("nome: Nome é obrigatório; email: E-mail inválido", message);
        }
    }

    @Nested
    class OutrasExceptions {

        @Test
        @DisplayName("Deve retornar 400 para MethodArgumentTypeMismatchException")
        void deveRetornar400ParaTypeMismatch() {
            MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);

            ResponseEntity<ApiError> response = handler.handleBadRequest(ex);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Parâmetro inválido na requisição.", response.getBody().message());
        }

        @Test
        @DisplayName("Deve retornar 400 para HttpMessageNotReadableException")
        void deveRetornar400ParaNotReadable() {
            HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

            ResponseEntity<ApiError> response = handler.handleNotReadable(ex);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Corpo da requisição inválido ou mal formatado.", response.getBody().message());
        }

        @Test
        @DisplayName("Deve retornar 404 para NoResourceFoundException")
        void deveRetornar404ParaNoResourceFound() throws Exception {
            NoResourceFoundException ex = mock(NoResourceFoundException.class);

            ResponseEntity<ApiError> response = handler.handleNoResourceFound(ex);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Recurso não encontrado.", response.getBody().message());
        }

        @Test
        @DisplayName("Deve retornar 500 para Exception genérica sem expor detalhes")
        void deveRetornar500ParaExcecaoGenerica() {
            Exception ex = new RuntimeException("Erro inesperado interno");

            ResponseEntity<ApiError> response = handler.handleGeneric(ex);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(500, response.getBody().status());
            assertEquals("Erro interno", response.getBody().message());
        }
    }
}

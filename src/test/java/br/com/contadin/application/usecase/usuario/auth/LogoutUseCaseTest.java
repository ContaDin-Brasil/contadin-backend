package br.com.contadin.application.usecase.usuario.auth;

import br.com.contadin.application.exception.auth.TokenInvalidoException;
import br.com.contadin.application.port.out.security.TokenBlacklistPort;
import br.com.contadin.application.port.out.security.TokenProviderPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    @Mock
    private TokenProviderPort tokenProviderPort;

    @Mock
    private TokenBlacklistPort tokenBlacklistPort;

    @InjectMocks
    private LogoutUseCase useCase;

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve realizar logout com token Bearer válido")
        void deveRealizarLogoutComBearerValido() {
            String authorizationHeader = "Bearer jwt-token";

            when(tokenProviderPort.validateToken("jwt-token"))
                    .thenReturn(true);

            assertDoesNotThrow(() ->
                    useCase.execute(authorizationHeader)
            );

            verify(tokenProviderPort)
                    .validateToken("jwt-token");

            verify(tokenBlacklistPort)
                    .blacklist("jwt-token");
        }

        @Test
        @DisplayName("Deve realizar logout com Bearer minúsculo")
        void deveRealizarLogoutComBearerMinusculo() {
            String authorizationHeader = "bearer jwt-token";

            when(tokenProviderPort.validateToken("jwt-token"))
                    .thenReturn(true);

            useCase.execute(authorizationHeader);

            verify(tokenProviderPort)
                    .validateToken("jwt-token");

            verify(tokenBlacklistPort)
                    .blacklist("jwt-token");
        }

        @Test
        @DisplayName("Deve realizar logout com token sem prefixo Bearer")
        void deveRealizarLogoutComTokenSemBearer() {
            String authorizationHeader = "jwt-token";

            when(tokenProviderPort.validateToken("jwt-token"))
                    .thenReturn(true);

            useCase.execute(authorizationHeader);

            verify(tokenProviderPort)
                    .validateToken("jwt-token");

            verify(tokenBlacklistPort)
                    .blacklist("jwt-token");
        }

        @Test
        @DisplayName("Deve remover aspas do token")
        void deveRemoverAspasDoToken() {
            String authorizationHeader = "\"jwt-token\"";

            when(tokenProviderPort.validateToken("jwt-token"))
                    .thenReturn(true);

            useCase.execute(authorizationHeader);

            verify(tokenProviderPort)
                    .validateToken("jwt-token");

            verify(tokenBlacklistPort)
                    .blacklist("jwt-token");
        }

        @Test
        @DisplayName("Deve remover espaços extras do token")
        void deveRemoverEspacosExtrasDoToken() {
            String authorizationHeader = "   Bearer    jwt-token   ";

            when(tokenProviderPort.validateToken("jwt-token"))
                    .thenReturn(true);

            useCase.execute(authorizationHeader);

            verify(tokenProviderPort)
                    .validateToken("jwt-token");

            verify(tokenBlacklistPort)
                    .blacklist("jwt-token");
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar exceção quando authorization header for null")
        void deveLancarExcecaoQuandoAuthorizationHeaderForNull() {
            TokenInvalidoException exception =
                    assertThrows(
                            TokenInvalidoException.class,
                            () -> useCase.execute(null)
                    );

            assertEquals(
                    "Token inválido",
                    exception.getMessage()
            );

            verify(tokenProviderPort, never())
                    .validateToken(anyString());

            verify(tokenBlacklistPort, never())
                    .blacklist(anyString());
        }

        @Test
        @DisplayName("Deve lançar exceção quando authorization header estiver vazio")
        void deveLancarExcecaoQuandoAuthorizationHeaderEstiverVazio() {
            TokenInvalidoException exception =
                    assertThrows(
                            TokenInvalidoException.class,
                            () -> useCase.execute("")
                    );

            assertEquals(
                    "Token inválido",
                    exception.getMessage()
            );

            verify(tokenProviderPort, never())
                    .validateToken(anyString());

            verify(tokenBlacklistPort, never())
                    .blacklist(anyString());
        }

        @Test
        @DisplayName("Deve lançar exceção quando authorization header tiver apenas espaços")
        void deveLancarExcecaoQuandoAuthorizationHeaderTiverApenasEspacos() {
            TokenInvalidoException exception =
                    assertThrows(
                            TokenInvalidoException.class,
                            () -> useCase.execute("     ")
                    );

            assertEquals(
                    "Token inválido",
                    exception.getMessage()
            );

            verify(tokenProviderPort, never())
                    .validateToken(anyString());

            verify(tokenBlacklistPort, never())
                    .blacklist(anyString());
        }

        @Test
        @DisplayName("Deve lançar exceção quando token for inválido")
        void deveLancarExcecaoQuandoTokenForInvalido() {
            String authorizationHeader = "Bearer jwt-token";

            when(tokenProviderPort.validateToken("jwt-token"))
                    .thenReturn(false);

            TokenInvalidoException exception =
                    assertThrows(
                            TokenInvalidoException.class,
                            () -> useCase.execute(authorizationHeader)
                    );

            assertEquals(
                    "Token inválido",
                    exception.getMessage()
            );

            verify(tokenProviderPort)
                    .validateToken("jwt-token");

            verify(tokenBlacklistPort, never())
                    .blacklist(anyString());
        }

        @Test
        @DisplayName("Deve validar token quando receber apenas Bearer")
        void deveValidarTokenQuandoReceberApenasBearer() {

            when(tokenProviderPort.validateToken("Bearer"))
                    .thenReturn(false);

            TokenInvalidoException exception =
                    assertThrows(
                            TokenInvalidoException.class,
                            () -> useCase.execute("Bearer ")
                    );

            assertEquals(
                    "Token inválido",
                    exception.getMessage()
            );

            verify(tokenProviderPort)
                    .validateToken("Bearer");

            verify(tokenBlacklistPort, never())
                    .blacklist(anyString());
        }

        @Test
        @DisplayName("Deve lançar exceção quando token possuir apenas aspas")
        void deveLancarExcecaoQuandoTokenPossuirApenasAspas() {
            TokenInvalidoException exception =
                    assertThrows(
                            TokenInvalidoException.class,
                            () -> useCase.execute("\"\"")
                    );

            assertEquals(
                    "Token inválido",
                    exception.getMessage()
            );

            verify(tokenProviderPort, never())
                    .validateToken(anyString());

            verify(tokenBlacklistPort, never())
                    .blacklist(anyString());
        }
    }
}
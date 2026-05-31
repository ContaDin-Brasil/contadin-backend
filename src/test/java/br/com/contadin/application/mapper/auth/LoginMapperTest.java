package br.com.contadin.application.mapper.auth;

import br.com.contadin.application.dto.usuario.auth.LoginResponse;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.domain.valueobject.Email;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginMapper")
class LoginMapperTest {

    /**
     * Implementação mínima da interface para testar o método default emailToString.
     * O MapStruct gera o método toResponse() em runtime — aqui retornamos null pois
     * não é o objeto de teste neste contexto.
     */
    private final LoginMapper mapper = new LoginMapper() {
        @Override
        public LoginResponse toResponse(Usuario usuario, String accessToken) {
            return null;
        }
    };

    @Nested
    @DisplayName("emailToString()")
    class EmailToString {

        @Test
        @DisplayName("deve retornar o valor string do email quando email é válido")
        void deveRetornarStringDoEmail() {
            Email email = new Email("usuario@contadin.com");

            String resultado = mapper.emailToString(email);

            assertEquals("usuario@contadin.com", resultado);
        }

        @Test
        @DisplayName("deve normalizar email para lowercase ao retornar")
        void deveNormalizarEmailParaLowercase() {
            Email email = new Email("USUARIO@CONTADIN.COM");

            String resultado = mapper.emailToString(email);

            assertEquals("usuario@contadin.com", resultado);
        }

        @Test
        @DisplayName("deve lançar NullPointerException quando email é null")
        void deveLancarNullPointerExceptionParaEmailNull() {
            NullPointerException ex = assertThrows(NullPointerException.class,
                    () -> mapper.emailToString(null));
            assertEquals("Email do usuário não pode ser nulo no login", ex.getMessage());
        }

        @Test
        @DisplayName("deve retornar string com domínio e usuário corretos")
        void deveRetornarEmailCompleto() {
            Email email = new Email("admin@empresa.com.br");

            String resultado = mapper.emailToString(email);

            assertTrue(resultado.contains("@"));
            assertEquals("admin@empresa.com.br", resultado);
        }

        @Test
        @DisplayName("deve retornar valor sem espaços (email é trimado no record)")
        void deveRetornarEmailSemEspacos() {
            Email email = new Email("  teste@contadin.com  ");

            String resultado = mapper.emailToString(email);

            assertEquals("teste@contadin.com", resultado);
            assertFalse(resultado.contains(" "));
        }
    }
}

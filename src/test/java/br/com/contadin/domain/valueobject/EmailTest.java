package br.com.contadin.domain.valueobject;

import br.com.contadin.domain.exception.usuario.EmailInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve criar Email com endereço válido")
        void deveCriarEmailValido() {
            Email email = new Email("usuario@email.com");
            assertEquals("usuario@email.com", email.valor());
        }

        @Test
        @DisplayName("Deve normalizar email para minúsculo")
        void deveNormalizarParaMinusculo() {
            Email email = new Email("USUARIO@EMAIL.COM");
            assertEquals("usuario@email.com", email.valor());
        }

        @Test
        @DisplayName("Deve remover espaços antes e depois do email")
        void deveRemoverEspacos() {
            Email email = new Email("  usuario@email.com  ");
            assertEquals("usuario@email.com", email.valor());
        }

        @Test
        @DisplayName("Deve normalizar email com maiúsculas e espaços juntos")
        void deveNormalizarMaiusculasEEspacos() {
            Email email = new Email("  USER@DOMAIN.COM  ");
            assertEquals("user@domain.com", email.valor());
        }

        @Test
        @DisplayName("Deve aceitar email com subdomínio")
        void deveAceitarEmailComSubdominio() {
            Email email = new Email("user@mail.contadin.com");
            assertEquals("user@mail.contadin.com", email.valor());
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar exceção quando email for nulo")
        void deveLancarExcecaoParaNull() {
            assertThrows(EmailInvalidoException.class, () -> new Email(null));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   "})
        @DisplayName("Deve lançar exceção quando email for vazio ou apenas espaços")
        void deveLancarExcecaoParaVazioOuEspacos(String valor) {
            assertThrows(EmailInvalidoException.class, () -> new Email(valor));
        }

        @ParameterizedTest
        @ValueSource(strings = {"usuario", "usuario.com", "usuarioarrobase"})
        @DisplayName("Deve lançar exceção quando email não contiver @")
        void deveLancarExcecaoSemArroba(String valor) {
            assertThrows(EmailInvalidoException.class, () -> new Email(valor));
        }
    }
}

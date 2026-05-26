package br.com.contadin.domain.valueobject;

import br.com.contadin.domain.exception.usuario.TelefoneInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TelefoneTest {

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve aceitar número de celular com 11 dígitos")
        void deveAceitarCelularCom11Digitos() {
            Telefone telefone = new Telefone("11999999999");
            assertEquals("11999999999", telefone.numero());
        }

        @Test
        @DisplayName("Deve aceitar número fixo com 10 dígitos")
        void deveAceitarFixoCom10Digitos() {
            Telefone telefone = new Telefone("1133334444");
            assertEquals("1133334444", telefone.numero());
        }

        @Test
        @DisplayName("Deve remover máscara do celular formatado")
        void deveRemoverMascaraDoCelular() {
            Telefone telefone = new Telefone("(11) 99999-9999");
            assertEquals("11999999999", telefone.numero());
        }

        @Test
        @DisplayName("Deve remover máscara do telefone fixo formatado")
        void deveRemoverMascaraDoFixo() {
            Telefone telefone = new Telefone("(11) 3333-4444");
            assertEquals("1133334444", telefone.numero());
        }

        @Test
        @DisplayName("Deve remover hífen e parênteses sem espaços")
        void deveRemoverHifenEParenteses() {
            Telefone telefone = new Telefone("(11)99999-9999");
            assertEquals("11999999999", telefone.numero());
        }

        @Nested
        class Formatacao {

            @Test
            @DisplayName("Deve formatar celular no padrão (DDD) XXXXX-XXXX")
            void deveFormatarCelular() {
                Telefone telefone = new Telefone("11999999999");
                assertEquals("(11) 99999-9999", telefone.formatado());
            }

            @Test
            @DisplayName("Deve formatar fixo no padrão (DDD) XXXX-XXXX")
            void deveFormatarFixo() {
                Telefone telefone = new Telefone("1133334444");
                assertEquals("(11) 3333-4444", telefone.formatado());
            }
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar exceção quando número for nulo")
        void deveLancarExcecaoParaNull() {
            assertThrows(TelefoneInvalidoException.class, () -> new Telefone(null));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   "})
        @DisplayName("Deve lançar exceção quando número for vazio ou apenas espaços")
        void deveLancarExcecaoParaVazioOuEspacos(String valor) {
            assertThrows(TelefoneInvalidoException.class, () -> new Telefone(valor));
        }

        @Test
        @DisplayName("Deve lançar exceção quando tiver menos de 10 dígitos")
        void deveLancarExcecaoComMenosDe10Digitos() {
            assertThrows(TelefoneInvalidoException.class, () -> new Telefone("119999999"));
        }

        @Test
        @DisplayName("Deve lançar exceção quando tiver mais de 11 dígitos")
        void deveLancarExcecaoComMaisDe11Digitos() {
            assertThrows(TelefoneInvalidoException.class, () -> new Telefone("119999999999"));
        }

        @Test
        @DisplayName("Deve lançar exceção para número com apenas letras")
        void deveLancarExcecaoParaLetras() {
            assertThrows(TelefoneInvalidoException.class, () -> new Telefone("abcdefghijk"));
        }
    }
}

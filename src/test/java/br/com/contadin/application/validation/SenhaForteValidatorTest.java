package br.com.contadin.application.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SenhaForteValidatorTest {

    private SenhaForteValidator validator;

    @BeforeEach
    void setup() {
        validator = new SenhaForteValidator();
    }

    @Nested
    class SenhasValidas {

        @Test
        @DisplayName("Deve retornar true para null (responsabilidade do @NotBlank)")
        void deveRetornarTrueParaNull() {
            assertTrue(validator.isValid(null, null));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "Contadin1@",
            "Segura!8x",
            "Pass@w0rd",
            "Abc$8x7y",
            "Min8Chr&1"
        })
        @DisplayName("Deve aceitar senha com 8+ chars, dígito e caractere especial sem sequências")
        void deveAceitarSenhasFortes(String senha) {
            assertTrue(validator.isValid(senha, null));
        }

        @Test
        @DisplayName("Deve aceitar senha com exatamente 8 caracteres")
        void deveAceitarSenhaCom8Caracteres() {
            assertTrue(validator.isValid("Abc@8x7z", null));
        }

        @Test
        @DisplayName("Deve aceitar todos os caracteres especiais permitidos: ! @ $ % &")
        void deveAceitarTodosEspeciaisPermitidos() {
            assertTrue(validator.isValid("Senha1!x", null));
            assertTrue(validator.isValid("Senha1@x", null));
            assertTrue(validator.isValid("Senha1$x", null));
            assertTrue(validator.isValid("Senha1%x", null));
            assertTrue(validator.isValid("Senha1&x", null));
        }
    }

    @Nested
    class SenhasMuitoCurtas {

        @ParameterizedTest
        @ValueSource(strings = {"Ab1@", "Ab1@5", "Ab1@567"})
        @DisplayName("Deve rejeitar senha com menos de 8 caracteres")
        void deveRejeitarSenhaCurta(String senha) {
            assertFalse(validator.isValid(senha, null));
        }
    }

    @Nested
    class SenhasSemDigito {

        @ParameterizedTest
        @ValueSource(strings = {"Abcdefg@", "Senha!Forte", "XPTO@abcd"})
        @DisplayName("Deve rejeitar senha sem dígito numérico")
        void deveRejeitarSenhaSemDigito(String senha) {
            assertFalse(validator.isValid(senha, null));
        }
    }

    @Nested
    class SenhasSemEspecial {

        @ParameterizedTest
        @ValueSource(strings = {"Abcdefg1", "Senha1234", "Password1"})
        @DisplayName("Deve rejeitar senha sem caractere especial [!@$%&]")
        void deveRejeitarSenhaSemEspecial(String senha) {
            assertFalse(validator.isValid(senha, null));
        }

        @Test
        @DisplayName("Deve rejeitar especial que não está na lista permitida (#, ^, *...)")
        void deveRejeitarEspecialNaoPermitido() {
            assertFalse(validator.isValid("Senha#1234", null));
            assertFalse(validator.isValid("Senha^1234", null));
            assertFalse(validator.isValid("Senha*1234", null));
        }
    }

    @Nested
    class SenhasComSequenciaNumerica {

        @ParameterizedTest
        @ValueSource(strings = {
            "Senha@012x",
            "Senha@123x",
            "Senha@234x",
            "Senha@345x",
            "Senha@456x",
            "Senha@567x",
            "Senha@678x",
            "Senha@789x",
            "Senha@987x",
            "Senha@876x",
            "Senha@765x",
            "Senha@654x",
            "Senha@543x",
            "Senha@432x",
            "Senha@321x",
            "Senha@210x"
        })
        @DisplayName("Deve rejeitar senha com sequência numérica crescente ou decrescente")
        void deveRejeitarSequenciaNumerica(String senha) {
            assertFalse(validator.isValid(senha, null));
        }
    }

    @Nested
    class SenhasComNumeroRepetido {

        @ParameterizedTest
        @ValueSource(strings = {
            "Senha@111",
            "Senha@222",
            "Senha@333",
            "Senha@444",
            "Senha@555",
            "Senha@666",
            "Senha@777",
            "Senha@888",
            "Senha@999",
            "Senha@000"
        })
        @DisplayName("Deve rejeitar senha com dígito repetido 3 vezes consecutivas")
        void deveRejeitarNumeroRepetido(String senha) {
            assertFalse(validator.isValid(senha, null));
        }
    }
}

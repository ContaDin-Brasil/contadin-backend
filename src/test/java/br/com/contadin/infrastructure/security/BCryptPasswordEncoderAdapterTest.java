package br.com.contadin.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BCryptPasswordEncoderAdapterTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BCryptPasswordEncoderAdapter adapter;

    @Nested
    class Encode {

        @Test
        @DisplayName("Deve delegar encode para o PasswordEncoder do Spring")
        void deveDelegarEncode() {
            when(passwordEncoder.encode("minha-senha")).thenReturn("$2a$10$hashGerado");

            String resultado = adapter.encode("minha-senha");

            assertEquals("$2a$10$hashGerado", resultado);
            verify(passwordEncoder).encode("minha-senha");
        }
    }

    @Nested
    class Matches {

        @Nested
        class ComHashBCrypt {

            @ParameterizedTest
            @ValueSource(strings = {"$2a$10$hash", "$2b$12$hash", "$2y$08$hash"})
            @DisplayName("Deve delegar matches ao PasswordEncoder quando senha for hash BCrypt")
            void deveDelegarMatchesParaBCrypt(String hashBCrypt) {
                when(passwordEncoder.matches("senha-plain", hashBCrypt)).thenReturn(true);

                assertTrue(adapter.matches("senha-plain", hashBCrypt));
                verify(passwordEncoder).matches("senha-plain", hashBCrypt);
            }

            @Test
            @DisplayName("Deve retornar false quando BCrypt não corresponder")
            void deveRetornarFalseQuandoBCryptNaoCorresponder() {
                String hash = "$2a$10$hashDiferente";
                when(passwordEncoder.matches("senha-errada", hash)).thenReturn(false);

                assertFalse(adapter.matches("senha-errada", hash));
            }
        }

        @Nested
        class ComTextoPlano {

            @Test
            @DisplayName("Deve comparar diretamente quando senha armazenada não for BCrypt")
            void deveCompararDiretamenteTextoPlano() {
                assertTrue(adapter.matches("senha-igual", "senha-igual"));
                verify(passwordEncoder, never()).matches("senha-igual", "senha-igual");
            }

            @Test
            @DisplayName("Deve retornar false quando senhas em texto plano forem diferentes")
            void deveRetornarFalseParaTextoPlanosDiferentes() {
                assertFalse(adapter.matches("senha-a", "senha-b"));
                verify(passwordEncoder, never()).matches("senha-a", "senha-b");
            }
        }

        @Nested
        class CasosDeErro {

            @Test
            @DisplayName("Deve retornar false quando rawPassword for nulo")
            void deveRetornarFalseParaRawPasswordNulo() {
                assertFalse(adapter.matches(null, "$2a$10$hash"));
                verify(passwordEncoder, never()).matches(null, "$2a$10$hash");
            }

            @Test
            @DisplayName("Deve retornar false quando encodedPassword for nulo")
            void deveRetornarFalseParaEncodedPasswordNulo() {
                assertFalse(adapter.matches("senha", null));
                verify(passwordEncoder, never()).matches("senha", null);
            }

            @Test
            @DisplayName("Deve retornar false quando ambas forem nulas")
            void deveRetornarFalseParaAmbaNulas() {
                assertFalse(adapter.matches(null, null));
            }
        }
    }
}

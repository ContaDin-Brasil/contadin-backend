package br.com.contadin.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {

    // Mínimo 32 chars para HMAC-SHA256
    private static final String SECRET = "contadin-test-secret-key-32chars!";
    private static final long EXPIRATION_MS = 3_600_000L; // 1 hora

    private JwtTokenProvider provider;

    @BeforeEach
    void setup() {
        provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "secret", SECRET);
        ReflectionTestUtils.setField(provider, "expirationMs", EXPIRATION_MS);
        provider.init();
    }

    @Nested
    class GenerateToken {

        @Test
        @DisplayName("Deve gerar token não nulo para um subject válido")
        void deveGerarTokenNaoNulo() {
            String token = provider.generateToken("usuario@email.com");
            assertNotNull(token);
        }

        @Test
        @DisplayName("Deve gerar token não vazio para um subject válido")
        void deveGerarTokenNaoVazio() {
            String token = provider.generateToken("usuario@email.com");
            assertFalse(token.isBlank());
        }

        @Test
        @DisplayName("Deve gerar tokens diferentes para subjects diferentes")
        void deveGerarTokensDiferentesParaSubjectsDiferentes() {
            String token1 = provider.generateToken("user1@email.com");
            String token2 = provider.generateToken("user2@email.com");
            assertFalse(token1.equals(token2));
        }
    }

    @Nested
    class ValidateToken {

        @Test
        @DisplayName("Deve validar token gerado pelo próprio provider como verdadeiro")
        void deveValidarTokenGeradoPeloProvider() {
            String token = provider.generateToken("usuario@email.com");
            assertTrue(provider.validateToken(token));
        }

        @Test
        @DisplayName("Deve retornar false para token com assinatura adulterada")
        void deveRetornarFalseParaTokenAdulterado() {
            String token = provider.generateToken("usuario@email.com");
            String adulterado = token.substring(0, token.length() - 5) + "XXXXX";
            assertFalse(provider.validateToken(adulterado));
        }

        @Test
        @DisplayName("Deve retornar false para token aleatório inválido")
        void deveRetornarFalseParaTokenInvalido() {
            assertFalse(provider.validateToken("nao.e.um.jwt.valido"));
        }

        @Test
        @DisplayName("Deve retornar false para token expirado")
        void deveRetornarFalseParaTokenExpirado() {
            JwtTokenProvider providerCurtoDuracao = new JwtTokenProvider();
            ReflectionTestUtils.setField(providerCurtoDuracao, "secret", SECRET);
            ReflectionTestUtils.setField(providerCurtoDuracao, "expirationMs", -1000L);
            providerCurtoDuracao.init();

            String tokenExpirado = providerCurtoDuracao.generateToken("usuario@email.com");

            assertFalse(provider.validateToken(tokenExpirado));
        }

        @Test
        @DisplayName("Deve retornar false para string vazia")
        void deveRetornarFalseParaStringVazia() {
            assertFalse(provider.validateToken(""));
        }
    }

    @Nested
    class GetSubject {

        @Test
        @DisplayName("Deve retornar o subject correto a partir do token gerado")
        void deveRetornarSubjectCorreto() {
            String subject = "usuario@email.com";
            String token = provider.generateToken(subject);

            assertEquals(subject, provider.getSubject(token));
        }

        @Test
        @DisplayName("Deve preservar o subject exato incluindo domínio do email")
        void devePreservarSubjectComDominio() {
            String subject = "gustavo.kohatsu@contadin.com.br";
            String token = provider.generateToken(subject);

            assertEquals(subject, provider.getSubject(token));
        }
    }
}

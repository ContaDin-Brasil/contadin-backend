package br.com.contadin.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTokenBlacklistAdapterTest {

    private InMemoryTokenBlacklistAdapter adapter;

    @BeforeEach
    void setup() {
        adapter = new InMemoryTokenBlacklistAdapter();
    }

    @Nested
    class Blacklist {

        @Test
        @DisplayName("Token inserido na blacklist deve ser identificado como revogado")
        void tokenBlacklistedDeveSerIdentificado() {
            adapter.blacklist("jwt-token");
            assertTrue(adapter.isBlacklisted("jwt-token"));
        }

        @Test
        @DisplayName("Token não inserido não deve ser identificado como revogado")
        void tokenNaoBlacklistedNaoDeveSerIdentificado() {
            assertFalse(adapter.isBlacklisted("jwt-token-ausente"));
        }

        @Test
        @DisplayName("Deve suportar múltiplos tokens na blacklist")
        void deveSuportarMultiplosTokens() {
            adapter.blacklist("token-1");
            adapter.blacklist("token-2");
            adapter.blacklist("token-3");

            assertTrue(adapter.isBlacklisted("token-1"));
            assertTrue(adapter.isBlacklisted("token-2"));
            assertTrue(adapter.isBlacklisted("token-3"));
        }

        @Test
        @DisplayName("Blacklist de um token não deve afetar outros tokens")
        void blacklistDeUmTokenNaoAfetaOutros() {
            adapter.blacklist("token-revogado");

            assertFalse(adapter.isBlacklisted("token-valido"));
        }

        @Test
        @DisplayName("Inserir mesmo token duas vezes não deve causar erro")
        void inserirMesmoTokenDuasVezes() {
            adapter.blacklist("token-duplicado");
            adapter.blacklist("token-duplicado");

            assertTrue(adapter.isBlacklisted("token-duplicado"));
        }

        @Test
        @DisplayName("Tokens distintos com conteúdo similar devem ser tratados independentemente")
        void tokensDistintosDevemSerIndependentes() {
            adapter.blacklist("token");

            assertFalse(adapter.isBlacklisted("token-extra"));
            assertFalse(adapter.isBlacklisted("xtoken"));
        }
    }
}

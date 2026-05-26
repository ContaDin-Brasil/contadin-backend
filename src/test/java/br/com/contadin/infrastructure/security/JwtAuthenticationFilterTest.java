package br.com.contadin.infrastructure.security;

import br.com.contadin.application.port.out.security.TokenBlacklistPort;
import br.com.contadin.application.port.out.security.TokenProviderPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.PrintWriter;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private TokenProviderPort tokenProviderPort;

    @Mock
    private TokenBlacklistPort tokenBlacklistPort;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private PrintWriter printWriter;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class SemToken {

        @Test
        @DisplayName("Deve passar para o próximo filtro quando não houver Authorization header")
        void devePassarParaProximoFiltroSemHeader() throws Exception {
            when(request.getHeader("Authorization")).thenReturn(null);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            verify(tokenProviderPort, never()).validateToken(anyString());
        }

        @Test
        @DisplayName("Deve passar para o próximo filtro quando Authorization header estiver vazio")
        void devePassarParaProximoFiltroComHeaderVazio() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("");

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            verify(tokenProviderPort, never()).validateToken(anyString());
        }

        @Test
        @DisplayName("Deve passar para o próximo filtro quando Authorization header tiver apenas espaços")
        void devePassarParaProximoFiltroComHeaderEspacos() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("   ");

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    class TokenRevogado {

        @Test
        @DisplayName("Deve retornar 401 e não prosseguir quando token estiver na blacklist")
        void deveRetornar401QuandoTokenRevogado() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer token-revogado");
            when(tokenBlacklistPort.isBlacklisted("token-revogado")).thenReturn(true);
            when(response.getWriter()).thenReturn(printWriter);
            when(objectMapper.writeValueAsString(any())).thenReturn("{}");

            filter.doFilterInternal(request, response, filterChain);

            verify(response).setStatus(401);
            verify(filterChain, never()).doFilter(request, response);
        }
    }

    @Nested
    class TokenInvalido {

        @Test
        @DisplayName("Deve retornar 401 e não prosseguir quando token for inválido")
        void deveRetornar401QuandoTokenInvalido() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer token-invalido");
            when(tokenBlacklistPort.isBlacklisted("token-invalido")).thenReturn(false);
            when(tokenProviderPort.validateToken("token-invalido")).thenReturn(false);
            when(response.getWriter()).thenReturn(printWriter);
            when(objectMapper.writeValueAsString(any())).thenReturn("{}");

            filter.doFilterInternal(request, response, filterChain);

            verify(response).setStatus(401);
            verify(filterChain, never()).doFilter(request, response);
        }
    }

    @Nested
    class TokenValido {

        @Test
        @DisplayName("Deve autenticar usuário e prosseguir quando token for válido")
        void deveAutenticarEProsseguirComTokenValido() throws Exception {
            UserDetails userDetails = User.builder()
                    .username("usuario@email.com")
                    .password("senha")
                    .authorities(Collections.emptyList())
                    .build();

            when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
            when(tokenBlacklistPort.isBlacklisted("token-valido")).thenReturn(false);
            when(tokenProviderPort.validateToken("token-valido")).thenReturn(true);
            when(tokenProviderPort.getSubject("token-valido")).thenReturn("usuario@email.com");
            when(userDetailsService.loadUserByUsername("usuario@email.com")).thenReturn(userDetails);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Deve aceitar token com prefixo Bearer maiúsculo")
        void deveAceitarTokenComBearerMaiusculo() throws Exception {
            UserDetails userDetails = User.builder()
                    .username("usuario@email.com")
                    .password("senha")
                    .authorities(Collections.emptyList())
                    .build();

            when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
            when(tokenBlacklistPort.isBlacklisted("token-valido")).thenReturn(false);
            when(tokenProviderPort.validateToken("token-valido")).thenReturn(true);
            when(tokenProviderPort.getSubject("token-valido")).thenReturn("usuario@email.com");
            when(userDetailsService.loadUserByUsername("usuario@email.com")).thenReturn(userDetails);

            filter.doFilterInternal(request, response, filterChain);

            verify(tokenProviderPort).validateToken("token-valido");
        }

        @Test
        @DisplayName("Deve aceitar token com prefixo bearer minúsculo")
        void deveAceitarTokenComBearerMinusculo() throws Exception {
            UserDetails userDetails = User.builder()
                    .username("usuario@email.com")
                    .password("senha")
                    .authorities(Collections.emptyList())
                    .build();

            when(request.getHeader("Authorization")).thenReturn("bearer token-valido");
            when(tokenBlacklistPort.isBlacklisted("token-valido")).thenReturn(false);
            when(tokenProviderPort.validateToken("token-valido")).thenReturn(true);
            when(tokenProviderPort.getSubject("token-valido")).thenReturn("usuario@email.com");
            when(userDetailsService.loadUserByUsername("usuario@email.com")).thenReturn(userDetails);

            filter.doFilterInternal(request, response, filterChain);

            verify(tokenProviderPort).validateToken("token-valido");
        }
    }

    @Nested
    class ExcecoesNoFiltro {

        @Test
        @DisplayName("Deve retornar 403 quando usuário estiver desabilitado (DisabledException)")
        void deveRetornar403QuandoUsuarioDesabilitado() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
            when(tokenBlacklistPort.isBlacklisted("token-valido")).thenReturn(false);
            when(tokenProviderPort.validateToken("token-valido")).thenReturn(true);
            when(tokenProviderPort.getSubject("token-valido")).thenReturn("usuario@email.com");
            when(userDetailsService.loadUserByUsername("usuario@email.com"))
                    .thenThrow(new org.springframework.security.authentication.DisabledException("Usuário inativo"));
            when(response.getWriter()).thenReturn(printWriter);
            when(objectMapper.writeValueAsString(any())).thenReturn("{}");

            filter.doFilterInternal(request, response, filterChain);

            verify(response).setStatus(403);
            verify(filterChain, never()).doFilter(request, response);
        }
    }

    @Nested
    class ExtrairToken {

        @Test
        @DisplayName("Deve processar token com prefixo Bearer duplicado (Bearer Bearer token)")
        void deveExtrairTokenComBearerDuplicado() throws Exception {
            UserDetails userDetails = User.builder()
                    .username("usuario@email.com")
                    .password("senha")
                    .authorities(Collections.emptyList())
                    .build();

            when(request.getHeader("Authorization")).thenReturn("Bearer Bearer token-valido");
            when(tokenBlacklistPort.isBlacklisted("token-valido")).thenReturn(false);
            when(tokenProviderPort.validateToken("token-valido")).thenReturn(true);
            when(tokenProviderPort.getSubject("token-valido")).thenReturn("usuario@email.com");
            when(userDetailsService.loadUserByUsername("usuario@email.com")).thenReturn(userDetails);

            filter.doFilterInternal(request, response, filterChain);

            verify(tokenProviderPort).validateToken("token-valido");
        }

        @Test
        @DisplayName("Deve extrair token quando valor estiver entre aspas duplas")
        void deveExtrairTokenEntreAspas() throws Exception {
            UserDetails userDetails = User.builder()
                    .username("usuario@email.com")
                    .password("senha")
                    .authorities(Collections.emptyList())
                    .build();

            when(request.getHeader("Authorization")).thenReturn("Bearer \"token-valido\"");
            when(tokenBlacklistPort.isBlacklisted("token-valido")).thenReturn(false);
            when(tokenProviderPort.validateToken("token-valido")).thenReturn(true);
            when(tokenProviderPort.getSubject("token-valido")).thenReturn("usuario@email.com");
            when(userDetailsService.loadUserByUsername("usuario@email.com")).thenReturn(userDetails);

            filter.doFilterInternal(request, response, filterChain);

            verify(tokenProviderPort).validateToken("token-valido");
        }
    }

    @Nested
    class AutenticacaoJaExistente {

        @Test
        @DisplayName("Deve pular autenticação quando SecurityContext já tiver autenticação")
        void devePassarParaFiltroQuandoAutenticacaoJaExistir() throws Exception {
            UserDetails userDetails = User.builder()
                    .username("usuario@email.com")
                    .password("senha")
                    .authorities(Collections.emptyList())
                    .build();

            // Pre-set authentication
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth =
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
            when(tokenBlacklistPort.isBlacklisted("token-valido")).thenReturn(false);
            when(tokenProviderPort.validateToken("token-valido")).thenReturn(true);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            verify(tokenProviderPort, never()).getSubject(anyString());
        }
    }

    @Nested
    class OutrasExcecoes {

        @Test
        @DisplayName("Deve retornar 401 quando AuthenticationException ocorrer")
        void deveRetornar401QuandoAuthenticationExceptionOcorrer() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
            when(tokenBlacklistPort.isBlacklisted("token-valido")).thenReturn(false);
            when(tokenProviderPort.validateToken("token-valido")).thenReturn(true);
            when(tokenProviderPort.getSubject("token-valido")).thenReturn("usuario@email.com");
            when(userDetailsService.loadUserByUsername("usuario@email.com"))
                    .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Credenciais inválidas"));
            when(response.getWriter()).thenReturn(printWriter);
            when(objectMapper.writeValueAsString(any())).thenReturn("{}");

            filter.doFilterInternal(request, response, filterChain);

            verify(response).setStatus(401);
            verify(filterChain, never()).doFilter(request, response);
        }

        @Test
        @DisplayName("Deve retornar 401 quando Exception genérica ocorrer")
        void deveRetornar401QuandoExcecaoGenericaOcorrer() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
            when(tokenBlacklistPort.isBlacklisted("token-valido")).thenReturn(false);
            when(tokenProviderPort.validateToken("token-valido")).thenReturn(true);
            when(tokenProviderPort.getSubject("token-valido")).thenReturn("usuario@email.com");
            when(userDetailsService.loadUserByUsername("usuario@email.com"))
                    .thenThrow(new RuntimeException("Erro inesperado"));
            when(response.getWriter()).thenReturn(printWriter);
            when(objectMapper.writeValueAsString(any())).thenReturn("{}");

            filter.doFilterInternal(request, response, filterChain);

            verify(response).setStatus(401);
            verify(filterChain, never()).doFilter(request, response);
        }
    }
}

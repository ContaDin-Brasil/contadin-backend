package br.com.contadin.infrastructure.security;

import br.com.contadin.application.port.out.security.TokenProviderPort;
import br.com.contadin.application.port.out.security.TokenBlacklistPort;
import br.com.contadin.infrastructure.web.exception.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenProviderPort tokenProviderPort;
    private final TokenBlacklistPort tokenBlacklistPort;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
            String token = extrairToken(authorizationHeader);

            if (token != null && tokenBlacklistPort.isBlacklisted(token)) {
                log.warn("JWT revogado para {} {}", request.getMethod(), request.getRequestURI());
                SecurityContextHolder.clearContext();
                escreverErroJson(response, HttpStatus.UNAUTHORIZED, "Token revogado");
                return;
            }

            if (token != null && !tokenProviderPort.validateToken(token)) {
                log.warn("JWT inválido para {} {}", request.getMethod(), request.getRequestURI());
                SecurityContextHolder.clearContext();
                escreverErroJson(response, HttpStatus.UNAUTHORIZED, "Token inválido");
                return;
            }

            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                String subject = tokenProviderPort.getSubject(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (DisabledException ex) {
            SecurityContextHolder.clearContext();
            log.warn("Acesso negado para usuário inativo em {} {}", request.getMethod(), request.getRequestURI());
            escreverErroJson(response, HttpStatus.FORBIDDEN, "Usuário inativo");
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            log.warn("Falha de autenticação em {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
            escreverErroJson(response, HttpStatus.UNAUTHORIZED, "Não autenticado");
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            log.error("Erro no filtro JWT em {} {}", request.getMethod(), request.getRequestURI(), ex);
            escreverErroJson(response, HttpStatus.UNAUTHORIZED, "Token inválido");
        }
    }

    private String extrairToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return null;
        }

        String valor = authorizationHeader.trim();

        // Aceita "Bearer <token>" com variação de caixa.
        if (valor.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            valor = valor.substring(BEARER_PREFIX.length()).trim();
        }

        // Protege contra input colado como "Bearer Bearer <token>".
        if (valor.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            valor = valor.substring(BEARER_PREFIX.length()).trim();
        }

        if (valor.startsWith("\"") && valor.endsWith("\"") && valor.length() > 1) {
            valor = valor.substring(1, valor.length() - 1);
        }

        return valor.isBlank() ? null : valor;
    }

    private void escreverErroJson(HttpServletResponse response, HttpStatus status, String mensagem) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ApiError.of(status.value(), mensagem)));
    }
}

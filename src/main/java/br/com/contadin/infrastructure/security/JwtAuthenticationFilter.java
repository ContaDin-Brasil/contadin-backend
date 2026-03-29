package br.com.contadin.infrastructure.security;

import br.com.contadin.application.port.out.TokenProviderPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenProviderPort tokenProviderPort;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        String token = extrairToken(authorizationHeader);

        if (token != null
                && tokenProviderPort.validateToken(token)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

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
}

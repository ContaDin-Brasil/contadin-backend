package br.com.contadin.application.usecase.usuario.auth;

import br.com.contadin.application.exception.auth.TokenInvalidoException;
import br.com.contadin.application.port.in.auth.LogoutInputPort;
import br.com.contadin.application.port.out.security.TokenBlacklistPort;
import br.com.contadin.application.port.out.security.TokenProviderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutUseCase implements LogoutInputPort {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String MSG_TOKEN_INVALIDO = "Token inválido";

    private final TokenProviderPort tokenProviderPort;
    private final TokenBlacklistPort tokenBlacklistPort;

    @Override
    public void execute(String authorizationHeader) {
        String token = extrairToken(authorizationHeader);

        if (!tokenProviderPort.validateToken(token)) {
            throw new TokenInvalidoException(MSG_TOKEN_INVALIDO);
        }

        tokenBlacklistPort.blacklist(token);
    }

    private String extrairToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new TokenInvalidoException(MSG_TOKEN_INVALIDO);
        }

        String value = authorizationHeader.trim();
        if (value.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            value = value.substring(BEARER_PREFIX.length()).trim();
        }

        if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
            value = value.substring(1, value.length() - 1);
        }

        if (value.isBlank()) {
            throw new TokenInvalidoException(MSG_TOKEN_INVALIDO);
        }

        return value;
    }
}

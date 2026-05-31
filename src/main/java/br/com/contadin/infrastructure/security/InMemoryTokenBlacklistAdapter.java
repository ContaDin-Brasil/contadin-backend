package br.com.contadin.infrastructure.security;

import br.com.contadin.application.port.out.security.TokenBlacklistPort;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryTokenBlacklistAdapter implements TokenBlacklistPort {

    private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();

    @Override
    public void blacklist(String token) {
        revokedTokens.add(token);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return revokedTokens.contains(token);
    }
}

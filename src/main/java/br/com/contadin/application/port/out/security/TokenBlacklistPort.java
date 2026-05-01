package br.com.contadin.application.port.out.security;

public interface TokenBlacklistPort {

    void blacklist(String token);

    boolean isBlacklisted(String token);
}

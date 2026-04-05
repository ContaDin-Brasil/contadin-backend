package br.com.contadin.application.port.out;

public interface TokenBlacklistPort {

    void blacklist(String token);

    boolean isBlacklisted(String token);
}

package br.com.contadin.application.port.out.security;

public interface TokenProviderPort {

    String generateToken(String subject);

    boolean validateToken(String token);

    String getSubject(String token);
}

package br.com.contadin.application.port.in.auth;

public interface LogoutInputPort {
    void execute(String authorizationHeader);
}

package br.com.contadin.application.port.in.usuario;

import java.util.UUID;

public interface DesativarUsuarioInputPort {
    void execute(UUID id);
}

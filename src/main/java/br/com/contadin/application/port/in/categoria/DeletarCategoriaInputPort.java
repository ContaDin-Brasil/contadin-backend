package br.com.contadin.application.port.in.categoria;

import java.util.UUID;

public interface DeletarCategoriaInputPort {
    void execute(UUID id);
}
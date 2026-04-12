package br.com.contadin.application.port.in.categoria;

import java.util.UUID;

public interface AlternarStatusCategoriaInputPort {
    void execute(UUID id);
}
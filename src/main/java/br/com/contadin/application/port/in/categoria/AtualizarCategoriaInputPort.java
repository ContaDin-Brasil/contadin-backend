package br.com.contadin.application.port.in.categoria;

import br.com.contadin.domain.model.Categoria;

import java.util.UUID;

public interface AtualizarCategoriaInputPort {
    Categoria execute(UUID id, Categoria categoria);
}
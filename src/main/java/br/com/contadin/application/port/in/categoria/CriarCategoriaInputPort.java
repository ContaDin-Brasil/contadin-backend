package br.com.contadin.application.port.in.categoria;

import br.com.contadin.domain.model.Categoria;

public interface CriarCategoriaInputPort {
    Categoria execute(Categoria categoria);
}

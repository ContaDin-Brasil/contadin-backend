package br.com.contadin.application.port.out;

import br.com.contadin.domain.model.Categoria;

public interface CategoriaRepository {
    Categoria save(Categoria categoria);
}

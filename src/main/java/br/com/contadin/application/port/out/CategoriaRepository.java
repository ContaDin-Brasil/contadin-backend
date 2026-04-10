package br.com.contadin.application.port.out;

import br.com.contadin.domain.model.Categoria;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaRepository {
    Categoria save(Categoria categoria);

    Optional<Categoria> findById(UUID id);

    List<Categoria> findByUsuario(UUID fkUsuario);

    List<Categoria> findByNomeAndUsuario(String nome, UUID fkUsuario);

    void deleteById(UUID id);
}

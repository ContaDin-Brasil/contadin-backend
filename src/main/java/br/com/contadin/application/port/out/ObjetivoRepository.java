package br.com.contadin.application.port.out;

import br.com.contadin.domain.model.Objetivo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ObjetivoRepository {
    Objetivo save(Objetivo objetivo);

    Optional<Objetivo> findById(UUID id);

    List<Objetivo> findByUsuario(UUID fkUsuario);

    List<Objetivo> findByNomeAndUsuario(String nome, UUID fkUsuario);

    void deleteById(UUID id);
}

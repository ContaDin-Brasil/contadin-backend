package br.com.contadin.application.port.out;

import br.com.contadin.domain.model.MetaGasto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MetaGastoRepository {
    MetaGasto save(MetaGasto metaGasto);

    Optional<MetaGasto> findById(UUID id);

    List<MetaGasto> findByUsuario(Integer fkUsuario);

    List<MetaGasto> findByNomeAndUsuario(String nome, Integer fkUsuario);

    void deleteById(UUID id);
}

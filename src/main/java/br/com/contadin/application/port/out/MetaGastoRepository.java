package br.com.contadin.application.port.out;

import br.com.contadin.domain.model.MetaGasto;

import java.util.List;
import java.util.Optional;

public interface MetaGastoRepository {
    MetaGasto save(MetaGasto metaGasto);

    Optional<MetaGasto> findById(Integer id);

    List<MetaGasto> findByUsuario(Integer fkUsuario);

    List<MetaGasto> findByNomeAndUsuario(String nome, Integer fkUsuario);

    void deleteById(Integer id);
}

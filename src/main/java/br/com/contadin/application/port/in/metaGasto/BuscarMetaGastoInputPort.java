package br.com.contadin.application.port.in.metaGasto;

import br.com.contadin.domain.model.MetaGasto;

import java.util.List;
import java.util.UUID;

public interface BuscarMetaGastoInputPort {
    List<MetaGasto> execute(Integer fkUsuario);

    MetaGasto executeBuscarPorId(UUID MetaGastoId);

    List<MetaGasto> executeBuscarPorNome(String nome, Integer fkUsuario);

}

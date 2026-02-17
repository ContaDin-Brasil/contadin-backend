package br.com.contadin.application.port.in.metaGasto;

import br.com.contadin.domain.model.MetaGasto;

import java.util.List;

public interface BuscarMetaGastoInputPort {
    List<MetaGasto> execute(Integer fkUsuario);

    MetaGasto executeBuscarPorId(Integer MetaGastoId);

    List<MetaGasto> executeBuscarPorNome(String nome, Integer fkUsuario);

}

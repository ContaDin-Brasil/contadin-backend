package br.com.contadin.application.port.in.metaGasto;

import br.com.contadin.domain.model.MetaGasto;

public interface AtualizarMetaGastoInputPort {
    MetaGasto execute(Integer id, MetaGasto metaGasto);
}

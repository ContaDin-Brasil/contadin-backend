package br.com.contadin.application.port.in.metaGasto;

import br.com.contadin.domain.model.MetaGasto;

import java.util.UUID;

public interface AtualizarMetaGastoInputPort {
    MetaGasto execute(UUID id, MetaGasto metaGasto);
}

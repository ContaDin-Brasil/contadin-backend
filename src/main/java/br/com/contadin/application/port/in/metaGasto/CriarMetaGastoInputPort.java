package br.com.contadin.application.port.in.metaGasto;

import br.com.contadin.domain.model.MetaGasto;

public interface CriarMetaGastoInputPort {
    MetaGasto execute(MetaGasto metaGasto);
}

package br.com.contadin.application.port.in.metaGasto;

import java.util.UUID;

public interface DeletarMetaGastoInputPort {
        void execute(UUID id);
}

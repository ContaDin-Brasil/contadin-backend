package br.com.contadin.application.port.in.transacao;

import java.util.List;
import java.util.UUID;

public interface DeletarMultiplasTransacoesInputPort {
    void execute(List<UUID> ids);
}

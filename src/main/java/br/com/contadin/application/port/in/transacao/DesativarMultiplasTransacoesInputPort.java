package br.com.contadin.application.port.in.transacao;

import java.util.List;
import java.util.UUID;

public interface DesativarMultiplasTransacoesInputPort {
    void execute(List<UUID> ids);
}

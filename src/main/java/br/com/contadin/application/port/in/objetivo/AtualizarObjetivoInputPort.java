package br.com.contadin.application.port.in.objetivo;

import br.com.contadin.domain.model.Objetivo;

import java.util.UUID;

public interface AtualizarObjetivoInputPort {
    Objetivo execute(UUID id, Objetivo objetivo);
}

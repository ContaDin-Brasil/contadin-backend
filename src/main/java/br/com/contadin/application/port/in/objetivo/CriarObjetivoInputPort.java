package br.com.contadin.application.port.in.objetivo;

import br.com.contadin.domain.model.Objetivo;

public interface CriarObjetivoInputPort {
    Objetivo execute(Objetivo objetivo);
}

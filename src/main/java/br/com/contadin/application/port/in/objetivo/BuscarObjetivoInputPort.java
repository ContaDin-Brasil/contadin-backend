package br.com.contadin.application.port.in.objetivo;

import br.com.contadin.domain.model.Objetivo;

import java.util.List;
import java.util.UUID;

public interface BuscarObjetivoInputPort {
    List<Objetivo> execute(UUID fkUsuario);

    Objetivo executeBuscarPorId(UUID objetivoId);

    List<Objetivo> executeBuscarPorNome(String nome, UUID fkUsuario);
}

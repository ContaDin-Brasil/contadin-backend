package br.com.contadin.application.port.in.objetivo;

import br.com.contadin.domain.enums.TipoObjetivo;
import br.com.contadin.domain.model.Objetivo;

import java.util.List;
import java.util.UUID;

public interface BuscarObjetivoInputPort {
    List<Objetivo> execute(UUID fkUsuario);

    List<Objetivo> execute(UUID fkUsuario, Boolean concluido, TipoObjetivo tipoObjetivo);

    Objetivo executeBuscarPorId(UUID objetivoId);

    List<Objetivo> executeBuscarPorNome(String nome, UUID fkUsuario);
}

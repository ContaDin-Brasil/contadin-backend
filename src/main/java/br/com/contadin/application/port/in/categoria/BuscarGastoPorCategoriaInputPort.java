package br.com.contadin.application.port.in.categoria;

import br.com.contadin.application.dto.categoria.GastoPorCategoriaResponse;

import java.util.List;
import java.util.UUID;

public interface BuscarGastoPorCategoriaInputPort {
    List<GastoPorCategoriaResponse> execute(UUID fkUsuario, int mes, int ano, UUID fkInstituicao);
}

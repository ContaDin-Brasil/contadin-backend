package br.com.contadin.application.port.in.instituicao;

import br.com.contadin.domain.model.Instituicao;

import java.util.List;
import java.util.UUID;

public interface BuscarInstituicaoInputPort {
    List<Instituicao> execute(UUID fkUsuario);

    Instituicao executeBuscarPorId(UUID instituicaoId);

    List<Instituicao> executeBuscarPorNome(UUID fkUsuario, String nome);
}

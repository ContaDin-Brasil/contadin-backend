package br.com.contadin.application.port.in.instituicao;

import br.com.contadin.domain.model.Instituicao;

import java.util.List;
import java.util.UUID;

public interface BuscarInstituicaoInputPort {
    List<Instituicao> execute(Integer fkUsuario);

    Instituicao executeBuscarPorId(UUID instituicaoId);

    List<Instituicao> executeBuscarPorNome(Integer fkUsuario, String nome);
}

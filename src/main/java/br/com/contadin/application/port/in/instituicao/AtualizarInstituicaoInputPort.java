package br.com.contadin.application.port.in.instituicao;

import br.com.contadin.domain.model.Instituicao;

import java.util.UUID;

public interface AtualizarInstituicaoInputPort {
    Instituicao execute(UUID id, Instituicao instituicao);

}

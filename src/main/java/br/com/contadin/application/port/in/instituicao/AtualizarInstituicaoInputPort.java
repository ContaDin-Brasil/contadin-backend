package br.com.contadin.application.port.in.instituicao;

import br.com.contadin.domain.model.Instituicao;

public interface AtualizarInstituicaoInputPort {
    Instituicao execute(Integer id, Instituicao instituicao);

}

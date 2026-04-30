package br.com.contadin.application.port.in.instituicao;

import br.com.contadin.domain.model.Instituicao;

public interface CriarInstituicaoInputPort {
    Instituicao execute(Instituicao instituicao);
}

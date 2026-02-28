package br.com.contadin.application.port.out;

import br.com.contadin.domain.model.Instituicao;

import java.util.List;
import java.util.Optional;

public interface InstituicaoRepository {

    Instituicao save(Instituicao instituicao);

    Optional<Instituicao> findById(Integer id);

    List<Instituicao> findAtivasByUsuario(Integer fkUsuario);

    List<Instituicao> findByNomeAndUsuario(Integer fkUsuario, String nome);
    
}


package br.com.contadin.application.port.out;

import br.com.contadin.domain.model.Instituicao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InstituicaoRepository {

    Instituicao save(Instituicao instituicao);

    Optional<Instituicao> findById(UUID id);

    List<Instituicao> findAtivasByUsuario(Integer fkUsuario);

    List<Instituicao> findByNomeAndUsuario(Integer fkUsuario, String nome);
    
}


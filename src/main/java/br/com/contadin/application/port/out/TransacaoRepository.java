package br.com.contadin.application.port.out;

import br.com.contadin.domain.model.Transacao;

import java.util.List;
import java.util.Optional;

public interface TransacaoRepository {
    Transacao save(Transacao transacao);

    Optional<Transacao> findById(Integer id);

    List<Transacao> findAll();

    void deleteById(Integer id);
}

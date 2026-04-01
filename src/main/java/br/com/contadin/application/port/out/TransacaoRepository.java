package br.com.contadin.application.port.out;

import br.com.contadin.application.dto.transacao.TransacaoFiltro;
import br.com.contadin.domain.model.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TransacaoRepository {
    Transacao save(Transacao transacao);

    Optional<Transacao> findById(Integer id);

    List<Transacao> findAll();

    Page<Transacao> findAll(TransacaoFiltro filtro, Pageable pageable);

    void deleteById(Integer id);
}

package br.com.contadin.application.port.out;

import br.com.contadin.application.dto.transacao.GastoCategoriaAgregado;
import br.com.contadin.application.dto.transacao.TransacaoFiltro;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.domain.model.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransacaoRepository {
    Transacao save(Transacao transacao);

    List<Transacao> saveAll(List<Transacao> transacoes);

    Optional<Transacao> findById(UUID id);

    List<Transacao> findAll();

    Page<Transacao> findAll(TransacaoFiltro filtro, Pageable pageable);

    void deleteById(UUID id);

    List<Transacao> findAllByIds(List<UUID> ids);

    void deleteAllByIds(List<UUID> ids);

    BigDecimal sumValorByCategoriaTipoEPeriodo(UUID fkCategoria, TipoTransacao tipo, LocalDateTime inicio, LocalDateTime fim);

    List<Transacao> findAllAtivasByInstituicao(UUID fkInstituicao);

    List<GastoCategoriaAgregado> buscarGastoPorCategoria(List<UUID> instituicaoIds, LocalDateTime dataInicio, LocalDateTime dataFim);
}

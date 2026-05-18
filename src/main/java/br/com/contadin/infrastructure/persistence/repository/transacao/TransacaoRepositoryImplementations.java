package br.com.contadin.infrastructure.persistence.repository.transacao;

import br.com.contadin.application.dto.transacao.GastoCategoriaAgregado;
import br.com.contadin.application.dto.transacao.TransacaoFiltro;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.domain.model.Transacao;
import br.com.contadin.infrastructure.persistence.mapper.TransacaoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TransacaoRepositoryImplementations implements TransacaoRepository {

     private final TransacaoJpaRepository jpaRepository;
     private final TransacaoPersistenceMapper persistenceMapper;

     @Override
     public List<Transacao> saveAll(List<Transacao> transacoes) {
          var entities = transacoes.stream().map(persistenceMapper::toEntity).toList();
          return jpaRepository.saveAll(entities).stream().map(persistenceMapper::toDomain).toList();
     }

     @Override
    public Transacao save(Transacao transacao) {
          var entity = persistenceMapper.toEntity(transacao);
          var saved = jpaRepository.save(entity);
          return persistenceMapper.toDomain(saved);
     }

     @Override
     public Optional<Transacao> findById(UUID id) {
          return jpaRepository.findById(id)
                    .map(persistenceMapper::toDomain);
     }

     @Override
     public List<Transacao> findAll() {
          return jpaRepository.findAll().stream()
                    .map(persistenceMapper::toDomain)
                    .toList();
     }

     @Override
     public Page<Transacao> findAll(TransacaoFiltro filtro, Pageable pageable) {
          return jpaRepository.findAll(TransacaoSpecifications.withFilters(filtro), pageable)
                    .map(persistenceMapper::toDomain);
     }

     @Override
     public void deleteById(UUID id) {
          jpaRepository.deleteById(id);
     }

     @Override
     public List<Transacao> findAllAtivasByInstituicao(UUID fkInstituicao) {
          return jpaRepository.findByFkInstituicaoAndAtivoTrue(fkInstituicao)
                    .stream().map(persistenceMapper::toDomain).toList();
     }

     @Override
     public List<GastoCategoriaAgregado> buscarGastoPorCategoria(List<UUID> instituicaoIds, LocalDateTime dataInicio, LocalDateTime dataFim) {
          return jpaRepository.buscarGastoPorCategoriaRaw(instituicaoIds, TipoTransacao.GASTO, dataInicio, dataFim)
                    .stream()
                    .map(row -> new GastoCategoriaAgregado(
                            (UUID) row[0],
                            row[1] != null ? BigDecimal.valueOf((Double) row[1]) : BigDecimal.ZERO
                    ))
                    .toList();
     }
}


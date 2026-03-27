package br.com.contadin.infrastructure.persistence.repository.transacao;

import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.model.Transacao;
import br.com.contadin.infrastructure.persistence.mapper.TransacaoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TransacaoRepositoryImplementations implements TransacaoRepository {

     private final TransacaoJpaRepository jpaRepository;
     private final TransacaoPersistenceMapper persistenceMapper;

     @Override
    public Transacao save(Transacao transacao) {
          var entity = persistenceMapper.toEntity(transacao);
          var saved = jpaRepository.save(entity);
          return persistenceMapper.toDomain(saved);
     }

     @Override
     public Optional<Transacao> findById(Integer id) {
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
     public void deleteById(Integer id) {
          jpaRepository.deleteById(id);
     }
}


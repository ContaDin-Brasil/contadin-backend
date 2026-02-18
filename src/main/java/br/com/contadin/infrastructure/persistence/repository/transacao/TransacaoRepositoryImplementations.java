package br.com.contadin.infrastructure.persistence.repository.transacao;

import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.model.Transacao;
import br.com.contadin.infrastructure.persistence.mapper.TransacaoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}


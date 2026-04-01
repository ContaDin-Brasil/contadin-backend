package br.com.contadin.infrastructure.persistence.repository.transacao;

import br.com.contadin.infrastructure.persistence.entity.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransacaoJpaRepository extends JpaRepository<TransacaoEntity, Integer>, JpaSpecificationExecutor<TransacaoEntity> {
}

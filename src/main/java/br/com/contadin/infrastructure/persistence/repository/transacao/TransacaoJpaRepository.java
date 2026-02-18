package br.com.contadin.infrastructure.persistence.repository.transacao;

import br.com.contadin.domain.model.Transacao;
import br.com.contadin.infrastructure.persistence.entity.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoJpaRepository extends JpaRepository<TransacaoEntity, Integer> {
}

package br.com.contadin.infrastructure.persistence.repository.transacao;

import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.infrastructure.persistence.entity.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TransacaoJpaRepository extends JpaRepository<TransacaoEntity, UUID>, JpaSpecificationExecutor<TransacaoEntity> {

    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM TransacaoEntity t " +
            "WHERE t.fkCategoria = :fkCategoria " +
            "AND t.tipo = :tipo " +
            "AND t.dataTransacao >= :inicio " +
            "AND t.dataTransacao <= :fim")
    Double sumValorByCategoriaTipoEPeriodo(
            @Param("fkCategoria") UUID fkCategoria,
            @Param("tipo") TipoTransacao tipo,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}


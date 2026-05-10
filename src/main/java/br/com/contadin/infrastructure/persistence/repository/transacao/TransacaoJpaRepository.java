package br.com.contadin.infrastructure.persistence.repository.transacao;

import br.com.contadin.application.projection.ReceitaMetricsProjection;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.infrastructure.persistence.entity.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TransacaoJpaRepository extends JpaRepository<TransacaoEntity, UUID>, JpaSpecificationExecutor<TransacaoEntity> {

    @Query("""
    SELECT new br.com.contadin.application.projection.ReceitaMetricsProjection(
        COALESCE(SUM(t.valor), 0.0),
        COUNT(t),
        COALESCE(AVG(t.valor), 0.0)
    )
    FROM TransacaoEntity t
    WHERE t.tipo = :tipo
    AND t.fkInstituicao IN (
        SELECT i.id
        FROM InstituicaoEntity i
        WHERE i.fkUsuario = :usuarioId
    )
    AND t.dataTransacao BETWEEN :inicio AND :fim
    """)
    ReceitaMetricsProjection buscarMetricasReceitaPeriodo(
            LocalDateTime inicio,
            LocalDateTime fim,
            TipoTransacao tipo,
            UUID usuarioId
    );
}

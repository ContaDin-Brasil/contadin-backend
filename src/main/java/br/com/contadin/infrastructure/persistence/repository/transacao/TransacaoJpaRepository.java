package br.com.contadin.infrastructure.persistence.repository.transacao;

import br.com.contadin.application.projection.ReceitaGastoMetricsProjection;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.infrastructure.persistence.entity.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransacaoJpaRepository extends JpaRepository<TransacaoEntity, UUID>, JpaSpecificationExecutor<TransacaoEntity> {

    @Query("""
    SELECT new br.com.contadin.application.projection.ReceitaGastoMetricsProjection(
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
    ReceitaGastoMetricsProjection buscarMetricasReceitaGastoPeriodo(
            LocalDateTime inicio,
            LocalDateTime fim,
            TipoTransacao tipo,
            UUID usuarioId
    );

    List<TransacaoEntity> findByFkInstituicaoAndAtivoTrue(UUID fkInstituicao);

    @Query("SELECT t.fkCategoria, SUM(t.valor) " +
           "FROM TransacaoEntity t " +
           "WHERE t.fkInstituicao IN :instituicaoIds " +
           "AND t.tipo = :tipo " +
           "AND t.ativo = true " +
           "AND t.dataTransacao >= :dataInicio " +
           "AND t.dataTransacao <= :dataFim " +
           "GROUP BY t.fkCategoria " +
           "ORDER BY SUM(t.valor) DESC")
    List<Object[]> buscarGastoPorCategoriaRaw(
            @Param("instituicaoIds") List<UUID> instituicaoIds,
            @Param("tipo") TipoTransacao tipo,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}

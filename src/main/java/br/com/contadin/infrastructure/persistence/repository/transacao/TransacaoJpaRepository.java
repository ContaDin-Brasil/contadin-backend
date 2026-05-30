package br.com.contadin.infrastructure.persistence.repository.transacao;

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

    List<TransacaoEntity> findByFkInstituicaoAndAtivoTrue(UUID fkInstituicao);

    @Query("SELECT t.fkCategoria, SUM(t.valor) " +
           "FROM TransacaoEntity t " +
           "WHERE t.fkInstituicao IN :instituicaoIds " +
           "AND t.tipo = :tipo " +
           "AND t.ativo = true " +
           "AND t.dataTransacao >= :dataInicio " +
           "AND t.dataTransacao < :dataFim " +
           "GROUP BY t.fkCategoria " +
           "ORDER BY SUM(t.valor) DESC")
    List<Object[]> buscarGastoPorCategoriaRaw(
            @Param("instituicaoIds") List<UUID> instituicaoIds,
            @Param("tipo") TipoTransacao tipo,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}

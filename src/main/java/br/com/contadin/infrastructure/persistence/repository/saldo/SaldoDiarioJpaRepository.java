package br.com.contadin.infrastructure.persistence.repository.saldo;

import br.com.contadin.infrastructure.persistence.entity.SaldoDiarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SaldoDiarioJpaRepository extends JpaRepository<SaldoDiarioEntity, UUID> {

    Optional<SaldoDiarioEntity> findFirstByFkInstituicaoAndDataLessThanOrderByDataDesc(UUID fkInstituicao, LocalDate data);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM SaldoDiarioEntity s WHERE s.fkInstituicao = :fkInstituicao AND s.data >= :data")
    void deletarAPartirDeData(@Param("fkInstituicao") UUID fkInstituicao, @Param("data") LocalDate data);

    List<SaldoDiarioEntity> findByFkInstituicaoAndDataBetweenOrderByData(UUID fkInstituicao, LocalDate inicio, LocalDate fim);

    List<SaldoDiarioEntity> findByFkUsuarioAndData(UUID fkUsuario, LocalDate data);

    List<SaldoDiarioEntity> findByFkInstituicaoAndData(UUID fkInstituicao, LocalDate data);

    @Query("SELECT s.data, SUM(s.saldoInicial), SUM(s.totalReceitas), SUM(s.totalGastos), SUM(s.saldoFinal) " +
           "FROM SaldoDiarioEntity s " +
           "WHERE s.fkUsuario = :fkUsuario AND s.data BETWEEN :inicio AND :fim " +
           "GROUP BY s.data ORDER BY s.data")
    List<Object[]> buscarConsolidadoRaw(@Param("fkUsuario") UUID fkUsuario,
                                        @Param("inicio") LocalDate inicio,
                                        @Param("fim") LocalDate fim);
}

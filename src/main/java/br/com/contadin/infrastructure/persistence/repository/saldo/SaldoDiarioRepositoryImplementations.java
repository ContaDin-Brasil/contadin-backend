package br.com.contadin.infrastructure.persistence.repository.saldo;

import br.com.contadin.application.port.out.SaldoDiarioRepository;
import br.com.contadin.domain.model.SaldoDiario;
import br.com.contadin.infrastructure.persistence.mapper.SaldoDiarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SaldoDiarioRepositoryImplementations implements SaldoDiarioRepository {

    private final SaldoDiarioJpaRepository jpaRepository;
    private final SaldoDiarioPersistenceMapper mapper;

    @Override
    public Optional<SaldoDiario> buscarUltimoAntesDeData(UUID fkInstituicao, LocalDate data) {
        return jpaRepository.findFirstByFkInstituicaoAndDataLessThanOrderByDataDesc(fkInstituicao, data)
                .map(mapper::toDomain);
    }

    @Override
    public void deletarAPartirDeData(UUID fkInstituicao, LocalDate data) {
        jpaRepository.deletarAPartirDeData(fkInstituicao, data);
    }

    @Override
    public List<SaldoDiario> salvarTodos(List<SaldoDiario> saldos) {
        var entities = saldos.stream().map(mapper::toEntity).toList();
        return jpaRepository.saveAll(entities).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<SaldoDiario> buscarPorInstituicaoEPeriodo(UUID fkInstituicao, LocalDate inicio, LocalDate fim) {
        return jpaRepository.findByFkInstituicaoAndDataBetweenOrderByData(fkInstituicao, inicio, fim)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<SaldoDiario> buscarConsolidadoPorUsuario(UUID fkUsuario, LocalDate inicio, LocalDate fim) {
        return jpaRepository.buscarConsolidadoRaw(fkUsuario, inicio, fim).stream()
                .map(row -> SaldoDiario.builder()
                        .data((LocalDate) row[0])
                        .saldoInicial((BigDecimal) row[1])
                        .totalReceitas((BigDecimal) row[2])
                        .totalGastos((BigDecimal) row[3])
                        .saldoFinal((BigDecimal) row[4])
                        .build())
                .toList();
    }

    @Override
    public List<SaldoDiario> buscarPorUsuarioNaData(UUID fkUsuario, LocalDate data) {
        return jpaRepository.findByFkUsuarioAndData(fkUsuario, data)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<SaldoDiario> buscarPorInstituicaoNaData(UUID fkInstituicao, LocalDate data) {
        return jpaRepository.findByFkInstituicaoAndData(fkInstituicao, data)
                .stream().map(mapper::toDomain).toList();
    }
}

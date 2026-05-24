package br.com.contadin.infrastructure.persistence.repository.saldo;

import br.com.contadin.domain.model.SaldoDiario;
import br.com.contadin.infrastructure.persistence.entity.SaldoDiarioEntity;
import br.com.contadin.infrastructure.persistence.mapper.SaldoDiarioPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaldoDiarioRepositoryImplementationsTest {

    @Mock
    SaldoDiarioJpaRepository jpaRepository;

    @Mock
    SaldoDiarioPersistenceMapper mapper;

    @InjectMocks
    SaldoDiarioRepositoryImplementations repo;

    @Test
    void deveBuscarUltimoSaldoAntesDeDataQuandoEncontrado() {
        UUID fkInstituicao = UUID.randomUUID();
        LocalDate data = LocalDate.now();
        SaldoDiarioEntity entity = mock(SaldoDiarioEntity.class);
        SaldoDiario domain = mock(SaldoDiario.class);
        when(jpaRepository.findFirstByFkInstituicaoAndDataLessThanOrderByDataDesc(fkInstituicao, data))
                .thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<SaldoDiario> result = repo.buscarUltimoAntesDeData(fkInstituicao, data);

        assertTrue(result.isPresent());
        assertEquals(domain, result.get());
        verify(jpaRepository).findFirstByFkInstituicaoAndDataLessThanOrderByDataDesc(fkInstituicao, data);
    }

    @Test
    void deveRetornarVazioQuandoUltimoSaldoNaoEncontrado() {
        UUID fkInstituicao = UUID.randomUUID();
        LocalDate data = LocalDate.now();
        when(jpaRepository.findFirstByFkInstituicaoAndDataLessThanOrderByDataDesc(fkInstituicao, data))
                .thenReturn(Optional.empty());

        Optional<SaldoDiario> result = repo.buscarUltimoAntesDeData(fkInstituicao, data);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findFirstByFkInstituicaoAndDataLessThanOrderByDataDesc(fkInstituicao, data);
    }

    @Test
    void deveDeletarSaldosAPartirDeData() {
        UUID fkInstituicao = UUID.randomUUID();
        LocalDate data = LocalDate.now();

        repo.deletarAPartirDeData(fkInstituicao, data);

        verify(jpaRepository).deletarAPartirDeData(fkInstituicao, data);
    }

    @Test
    void deveSalvarTodosOsSaldos() {
        SaldoDiarioEntity entity = mock(SaldoDiarioEntity.class);
        SaldoDiarioEntity savedEntity = mock(SaldoDiarioEntity.class);
        SaldoDiario domain = mock(SaldoDiario.class);
        SaldoDiario savedDomain = mock(SaldoDiario.class);
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.saveAll(List.of(entity))).thenReturn(List.of(savedEntity));
        when(mapper.toDomain(savedEntity)).thenReturn(savedDomain);

        List<SaldoDiario> result = repo.salvarTodos(List.of(domain));

        assertEquals(List.of(savedDomain), result);
        verify(jpaRepository).saveAll(List.of(entity));
        verify(mapper).toDomain(savedEntity);
    }

    @Test
    void deveBuscarSaldosPorInstituicaoEPeriodo() {
        UUID fkInstituicao = UUID.randomUUID();
        LocalDate inicio = LocalDate.now().minusDays(30);
        LocalDate fim = LocalDate.now();
        SaldoDiarioEntity entity = mock(SaldoDiarioEntity.class);
        SaldoDiario domain = mock(SaldoDiario.class);
        when(jpaRepository.findByFkInstituicaoAndDataBetweenOrderByData(fkInstituicao, inicio, fim))
                .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<SaldoDiario> result = repo.buscarPorInstituicaoEPeriodo(fkInstituicao, inicio, fim);

        assertEquals(List.of(domain), result);
        verify(jpaRepository).findByFkInstituicaoAndDataBetweenOrderByData(fkInstituicao, inicio, fim);
    }

    @Test
    void deveBuscarConsolidadoPorUsuarioMapeandoLinhas() {
        UUID fkUsuario = UUID.randomUUID();
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 1, 31);

        LocalDate data = LocalDate.of(2026, 1, 15);
        BigDecimal saldoInicial = BigDecimal.valueOf(1000);
        BigDecimal totalReceitas = BigDecimal.valueOf(500);
        BigDecimal totalGastos = BigDecimal.valueOf(200);
        BigDecimal saldoFinal = BigDecimal.valueOf(1300);
        Object[] row = {data, saldoInicial, totalReceitas, totalGastos, saldoFinal};
        List<Object[]> rawRows = new java.util.ArrayList<>();
        rawRows.add(row);

        when(jpaRepository.buscarConsolidadoRaw(fkUsuario, inicio, fim)).thenReturn(rawRows);

        List<SaldoDiario> result = repo.buscarConsolidadoPorUsuario(fkUsuario, inicio, fim);

        assertEquals(1, result.size());
        SaldoDiario saldo = result.get(0);
        assertEquals(data, saldo.getData());
        assertEquals(saldoInicial, saldo.getSaldoInicial());
        assertEquals(totalReceitas, saldo.getTotalReceitas());
        assertEquals(totalGastos, saldo.getTotalGastos());
        assertEquals(saldoFinal, saldo.getSaldoFinal());
        verify(jpaRepository).buscarConsolidadoRaw(fkUsuario, inicio, fim);
    }

    @Test
    void deveBuscarConsolidadoPorUsuarioRetornandoListaVazia() {
        UUID fkUsuario = UUID.randomUUID();
        LocalDate inicio = LocalDate.now().minusDays(30);
        LocalDate fim = LocalDate.now();
        when(jpaRepository.buscarConsolidadoRaw(fkUsuario, inicio, fim)).thenReturn(List.of());

        List<SaldoDiario> result = repo.buscarConsolidadoPorUsuario(fkUsuario, inicio, fim);

        assertTrue(result.isEmpty());
    }

    @Test
    void deveBuscarSaldosPorUsuarioNaData() {
        UUID fkUsuario = UUID.randomUUID();
        LocalDate data = LocalDate.now();
        SaldoDiarioEntity entity = mock(SaldoDiarioEntity.class);
        SaldoDiario domain = mock(SaldoDiario.class);
        when(jpaRepository.findByFkUsuarioAndData(fkUsuario, data)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<SaldoDiario> result = repo.buscarPorUsuarioNaData(fkUsuario, data);

        assertEquals(List.of(domain), result);
        verify(jpaRepository).findByFkUsuarioAndData(fkUsuario, data);
    }

    @Test
    void deveBuscarSaldosPorInstituicaoNaData() {
        UUID fkInstituicao = UUID.randomUUID();
        LocalDate data = LocalDate.now();
        SaldoDiarioEntity entity = mock(SaldoDiarioEntity.class);
        SaldoDiario domain = mock(SaldoDiario.class);
        when(jpaRepository.findByFkInstituicaoAndData(fkInstituicao, data)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<SaldoDiario> result = repo.buscarPorInstituicaoNaData(fkInstituicao, data);

        assertEquals(List.of(domain), result);
        verify(jpaRepository).findByFkInstituicaoAndData(fkInstituicao, data);
    }
}

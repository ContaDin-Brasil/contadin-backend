package br.com.contadin.infrastructure.persistence.repository.transacao;

import br.com.contadin.application.dto.transacao.GastoCategoriaAgregado;
import br.com.contadin.application.dto.transacao.TransacaoFiltro;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.domain.model.Transacao;
import br.com.contadin.infrastructure.persistence.entity.TransacaoEntity;
import br.com.contadin.infrastructure.persistence.mapper.TransacaoPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoRepositoryImplementationsTest {

    @Mock
    TransacaoJpaRepository jpaRepository;

    @Mock
    TransacaoPersistenceMapper persistenceMapper;

    @InjectMocks
    TransacaoRepositoryImplementations repo;

    @Test
    void deveSalvarTodasAsTransacoes() {
        TransacaoEntity entity = mock(TransacaoEntity.class);
        Transacao domain = mock(Transacao.class);
        when(persistenceMapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.saveAll(List.of(entity))).thenReturn(List.of(entity));
        when(persistenceMapper.toDomain(entity)).thenReturn(domain);

        List<Transacao> result = repo.saveAll(List.of(domain));

        assertEquals(List.of(domain), result);
        verify(jpaRepository).saveAll(List.of(entity));
        verify(persistenceMapper).toDomain(entity);
    }

    @Test
    void deveSalvarUmaTransacao() {
        TransacaoEntity entity = mock(TransacaoEntity.class);
        TransacaoEntity saved = mock(TransacaoEntity.class);
        Transacao domain = mock(Transacao.class);
        Transacao savedDomain = mock(Transacao.class);
        when(persistenceMapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(saved);
        when(persistenceMapper.toDomain(saved)).thenReturn(savedDomain);

        Transacao result = repo.save(domain);

        assertEquals(savedDomain, result);
        verify(jpaRepository).save(entity);
        verify(persistenceMapper).toDomain(saved);
    }

    @Test
    void deveBuscarPorIdQuandoEncontrado() {
        UUID id = UUID.randomUUID();
        TransacaoEntity entity = mock(TransacaoEntity.class);
        Transacao domain = mock(Transacao.class);
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(persistenceMapper.toDomain(entity)).thenReturn(domain);

        Optional<Transacao> result = repo.findById(id);

        assertTrue(result.isPresent());
        assertEquals(domain, result.get());
        verify(jpaRepository).findById(id);
    }

    @Test
    void deveRetornarVazioQuandoNaoEncontradoPorId() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Transacao> result = repo.findById(id);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findById(id);
    }

    @Test
    void deveBuscarTodasAsTransacoes() {
        TransacaoEntity entity = mock(TransacaoEntity.class);
        Transacao domain = mock(Transacao.class);
        when(jpaRepository.findAll()).thenReturn(List.of(entity));
        when(persistenceMapper.toDomain(entity)).thenReturn(domain);

        List<Transacao> result = repo.findAll();

        assertEquals(List.of(domain), result);
        verify(jpaRepository).findAll();
    }

    @Test
    @SuppressWarnings("unchecked")
    void deveBuscarComFiltroEPaginacao() {
        Page<TransacaoEntity> entityPage = mock(Page.class);
        Page<Transacao> domainPage = mock(Page.class);
        TransacaoFiltro filtro = new TransacaoFiltro(null, null, null, null, null, null, null, null, null, null);
        Pageable pageable = mock(Pageable.class);

        when(jpaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(entityPage);
        doReturn(domainPage).when(entityPage).map(any());

        Page<Transacao> result = repo.findAll(filtro, pageable);

        assertEquals(domainPage, result);
        verify(jpaRepository).findAll(any(Specification.class), eq(pageable));
        verify(entityPage).map(any());
    }

    @Test
    void deveDeletarPorId() {
        UUID id = UUID.randomUUID();

        repo.deleteById(id);

        verify(jpaRepository).deleteById(id);
    }

    @Test
    void deveSomarValorQuandoResultadoNaoNulo() {
        UUID fkCategoria = UUID.randomUUID();
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();
        when(jpaRepository.sumValorByCategoriaTipoEPeriodo(fkCategoria, TipoTransacao.GASTO, inicio, fim))
                .thenReturn(150.50);

        BigDecimal result = repo.sumValorByCategoriaTipoEPeriodo(fkCategoria, TipoTransacao.GASTO, inicio, fim);

        assertEquals(BigDecimal.valueOf(150.50), result);
        verify(jpaRepository).sumValorByCategoriaTipoEPeriodo(fkCategoria, TipoTransacao.GASTO, inicio, fim);
    }

    @Test
    void deveSomarZeroQuandoResultadoNulo() {
        UUID fkCategoria = UUID.randomUUID();
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();
        when(jpaRepository.sumValorByCategoriaTipoEPeriodo(fkCategoria, TipoTransacao.GASTO, inicio, fim))
                .thenReturn(null);

        BigDecimal result = repo.sumValorByCategoriaTipoEPeriodo(fkCategoria, TipoTransacao.GASTO, inicio, fim);

        assertEquals(0, result.compareTo(BigDecimal.ZERO));
        verify(jpaRepository).sumValorByCategoriaTipoEPeriodo(fkCategoria, TipoTransacao.GASTO, inicio, fim);
    }

    @Test
    void deveBuscarAtivasPorInstituicao() {
        UUID fkInstituicao = UUID.randomUUID();
        TransacaoEntity entity = mock(TransacaoEntity.class);
        Transacao domain = mock(Transacao.class);
        when(jpaRepository.findByFkInstituicaoAndAtivoTrue(fkInstituicao)).thenReturn(List.of(entity));
        when(persistenceMapper.toDomain(entity)).thenReturn(domain);

        List<Transacao> result = repo.findAllAtivasByInstituicao(fkInstituicao);

        assertEquals(List.of(domain), result);
        verify(jpaRepository).findByFkInstituicaoAndAtivoTrue(fkInstituicao);
    }

    @Test
    void deveBuscarGastoPorCategoriaComValorNaoNulo() {
        List<UUID> ids = List.of(UUID.randomUUID());
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();
        UUID fkCategoria = UUID.randomUUID();
        Object[] row = {fkCategoria, 200.0};
        List<Object[]> rawResult = new java.util.ArrayList<>();
        rawResult.add(row);
        when(jpaRepository.buscarGastoPorCategoriaRaw(ids, TipoTransacao.GASTO, inicio, fim))
                .thenReturn(rawResult);

        List<GastoCategoriaAgregado> result = repo.buscarGastoPorCategoria(ids, inicio, fim);

        assertEquals(1, result.size());
        assertEquals(fkCategoria, result.get(0).fkCategoria());
        assertEquals(BigDecimal.valueOf(200.0), result.get(0).total());
        verify(jpaRepository).buscarGastoPorCategoriaRaw(ids, TipoTransacao.GASTO, inicio, fim);
    }

    @Test
    void deveBuscarGastoPorCategoriaComValorNulo() {
        List<UUID> ids = List.of(UUID.randomUUID());
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();
        UUID fkCategoria = UUID.randomUUID();
        Object[] row = {fkCategoria, null};
        List<Object[]> rawResult = new java.util.ArrayList<>();
        rawResult.add(row);
        when(jpaRepository.buscarGastoPorCategoriaRaw(ids, TipoTransacao.GASTO, inicio, fim))
                .thenReturn(rawResult);

        List<GastoCategoriaAgregado> result = repo.buscarGastoPorCategoria(ids, inicio, fim);

        assertEquals(1, result.size());
        assertEquals(fkCategoria, result.get(0).fkCategoria());
        assertEquals(BigDecimal.ZERO, result.get(0).total());
    }
}

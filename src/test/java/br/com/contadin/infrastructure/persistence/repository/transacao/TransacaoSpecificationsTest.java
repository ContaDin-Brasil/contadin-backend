package br.com.contadin.infrastructure.persistence.repository.transacao;

import br.com.contadin.application.dto.transacao.TransacaoFiltro;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.infrastructure.persistence.entity.TransacaoEntity;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked", "rawtypes"})
@ExtendWith(MockitoExtension.class)
class TransacaoSpecificationsTest {

    @Mock Root<TransacaoEntity> root;
    @Mock CriteriaQuery<?> query;
    @Mock CriteriaBuilder cb;
    @Mock Predicate predicate;
    @Mock Path path;
    @Mock Expression<String> lowerExpression;

    @BeforeEach
    void configurarMocks() {
        lenient().when(root.get(anyString())).thenReturn(path);
        lenient().when(cb.equal(any(), any())).thenReturn(predicate);
        lenient().when(cb.greaterThanOrEqualTo(any(), (Comparable) any())).thenReturn(predicate);
        lenient().when(cb.lessThanOrEqualTo(any(), (Comparable) any())).thenReturn(predicate);
        lenient().when(cb.isNotNull(any())).thenReturn(predicate);
        lenient().when(cb.isNull(any())).thenReturn(predicate);
        lenient().when(cb.lower(any())).thenReturn(lowerExpression);
        lenient().when(cb.like(any(Expression.class), anyString())).thenReturn(predicate);
        lenient().when(cb.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
    }

    @Test
    void deveRetornarSpecNaoNula() {
        Specification<TransacaoEntity> spec = TransacaoSpecifications.withFilters(null);
        assertNotNull(spec);
    }

    @Test
    void deveRetornarPredicadoNuloQuandoFiltroNulo() {
        Specification<TransacaoEntity> spec = TransacaoSpecifications.withFilters(null);
        Predicate result = spec.toPredicate(root, query, cb);
        assertNull(result);
    }

    @Test
    void deveFiltrarPorTipo() {
        TransacaoFiltro filtro = new TransacaoFiltro(
                TipoTransacao.GASTO, null, null, null, null, null, null, null, null, null);

        TransacaoSpecifications.withFilters(filtro).toPredicate(root, query, cb);

        verify(root).get("tipo");
        verify(cb).equal(path, TipoTransacao.GASTO);
    }

    @Test
    void deveFiltrarPorFkInstituicao() {
        UUID fkInstituicao = UUID.randomUUID();
        TransacaoFiltro filtro = new TransacaoFiltro(
                null, fkInstituicao, null, null, null, null, null, null, null, null);

        TransacaoSpecifications.withFilters(filtro).toPredicate(root, query, cb);

        verify(root).get("fkInstituicao");
        verify(cb).equal(path, fkInstituicao);
    }

    @Test
    void deveFiltrarPorFkCategoria() {
        UUID fkCategoria = UUID.randomUUID();
        TransacaoFiltro filtro = new TransacaoFiltro(
                null, null, fkCategoria, null, null, null, null, null, null, null);

        TransacaoSpecifications.withFilters(filtro).toPredicate(root, query, cb);

        verify(root).get("fkCategoria");
        verify(cb).equal(path, fkCategoria);
    }

    @Test
    void deveFiltrarPorValorGte() {
        Double valorGte = 100.0;
        TransacaoFiltro filtro = new TransacaoFiltro(
                null, null, null, valorGte, null, null, null, null, null, null);

        TransacaoSpecifications.withFilters(filtro).toPredicate(root, query, cb);

        verify(root).get("valor");
        verify(cb).greaterThanOrEqualTo(any(), eq(valorGte));
    }

    @Test
    void deveFiltrarPorValorLte() {
        Double valorLte = 500.0;
        TransacaoFiltro filtro = new TransacaoFiltro(
                null, null, null, null, valorLte, null, null, null, null, null);

        TransacaoSpecifications.withFilters(filtro).toPredicate(root, query, cb);

        verify(root).get("valor");
        verify(cb).lessThanOrEqualTo(any(), eq(valorLte));
    }

    @Test
    void deveFiltrarPorParcelado() {
        TransacaoFiltro filtro = new TransacaoFiltro(
                null, null, null, null, null, true, null, null, null, null);

        TransacaoSpecifications.withFilters(filtro).toPredicate(root, query, cb);

        verify(root).get("parcelado");
        verify(cb).equal(path, true);
    }

    @Test
    void deveFiltrarPorRecorrenteTrue() {
        TransacaoFiltro filtro = new TransacaoFiltro(
                null, null, null, null, null, null, true, null, null, null);

        TransacaoSpecifications.withFilters(filtro).toPredicate(root, query, cb);

        verify(root).get("recorrencia");
        verify(cb).isNotNull(path);
    }

    @Test
    void deveFiltrarPorRecorrenteFalse() {
        TransacaoFiltro filtro = new TransacaoFiltro(
                null, null, null, null, null, null, false, null, null, null);

        TransacaoSpecifications.withFilters(filtro).toPredicate(root, query, cb);

        verify(root).get("recorrencia");
        verify(cb).isNull(path);
    }

    @Test
    void deveFiltrarPorDataTransacaoGte() {
        LocalDateTime dataGte = LocalDateTime.now().minusDays(30);
        TransacaoFiltro filtro = new TransacaoFiltro(
                null, null, null, null, null, null, null, dataGte, null, null);

        TransacaoSpecifications.withFilters(filtro).toPredicate(root, query, cb);

        verify(root).get("dataTransacao");
        verify(cb).greaterThanOrEqualTo(any(), eq(dataGte));
    }

    @Test
    void deveFiltrarPorDataTransacaoLte() {
        LocalDateTime dataLte = LocalDateTime.now();
        TransacaoFiltro filtro = new TransacaoFiltro(
                null, null, null, null, null, null, null, null, dataLte, null);

        TransacaoSpecifications.withFilters(filtro).toPredicate(root, query, cb);

        verify(root).get("dataTransacao");
        verify(cb).lessThanOrEqualTo(any(), eq(dataLte));
    }

    @Test
    void deveFiltrarPorSearch() {
        TransacaoFiltro filtro = new TransacaoFiltro(
                null, null, null, null, null, null, null, null, null, "mercado");

        TransacaoSpecifications.withFilters(filtro).toPredicate(root, query, cb);

        verify(root).get("descricao");
        verify(cb).lower(path);
        verify(cb).like(lowerExpression, "%mercado%");
    }

    @Test
    void deveIgnorarSearchEmBranco() {
        TransacaoFiltro filtro = new TransacaoFiltro(
                null, null, null, null, null, null, null, null, null, "   ");

        TransacaoSpecifications.withFilters(filtro).toPredicate(root, query, cb);

        verify(cb, never()).lower(any());
        verify(cb, never()).like(any(Expression.class), anyString());
    }

    @Test
    void deveIgnorarSearchNulo() {
        TransacaoFiltro filtro = new TransacaoFiltro(
                null, null, null, null, null, null, null, null, null, null);

        TransacaoSpecifications.withFilters(filtro).toPredicate(root, query, cb);

        verify(cb, never()).lower(any());
        verify(cb, never()).like(any(Expression.class), anyString());
    }

    @Test
    void deveRetornarSpecNaoNulaParaFiltroCompletoPreenchido() {
        UUID instId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        LocalDateTime gte = LocalDateTime.now().minusDays(30);
        LocalDateTime lte = LocalDateTime.now();
        TransacaoFiltro filtro = new TransacaoFiltro(
                TipoTransacao.GASTO, instId, catId, 10.0, 500.0, false, true, gte, lte, "supermercado");

        Specification<TransacaoEntity> spec = TransacaoSpecifications.withFilters(filtro);
        assertNotNull(spec);
        assertDoesNotThrow(() -> spec.toPredicate(root, query, cb));
    }
}

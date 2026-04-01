package br.com.contadin.application.usecase.transacao;

import br.com.contadin.application.dto.transacao.TransacaoConsultaParams;
import br.com.contadin.application.dto.transacao.TransacaoFiltro;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.domain.exception.transacao.TransacaoInvalidaException;
import br.com.contadin.domain.model.Transacao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarTransacaoUseCaseTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private BuscarTransacaoUseCase useCase;

    @Test
    void deveUsarOrdenacaoDefaultDataTransacaoDescComPaginacaoPadrao() {
        Page<Transacao> page = new PageImpl<>(List.of(Transacao.builder().id(1).build()));
        when(transacaoRepository.findAll(any(TransacaoFiltro.class), any(Pageable.class))).thenReturn(page);

        TransacaoConsultaParams params = new TransacaoConsultaParams(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        useCase.execute(params);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(transacaoRepository).findAll(any(TransacaoFiltro.class), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());

        Sort.Order order = pageable.getSort().getOrderFor("dataTransacao");
        assertNotNull(order);
        assertEquals(Sort.Direction.DESC, order.getDirection());
    }

    @Test
    void deveAplicarFiltrosCombinadosPorAnd() {
        when(transacaoRepository.findAll(any(TransacaoFiltro.class), any(Pageable.class))).thenReturn(Page.empty());

        TransacaoConsultaParams params = new TransacaoConsultaParams(
                2,
                20,
                "valor",
                "asc",
                TipoTransacao.GASTO,
                10,
                15,
                100.0,
                250.0,
                true,
                true,
                "2026-03-01",
                "2026-03-31",
                "mercado"
        );

        useCase.execute(params);

        ArgumentCaptor<TransacaoFiltro> filtroCaptor = ArgumentCaptor.forClass(TransacaoFiltro.class);
        verify(transacaoRepository).findAll(filtroCaptor.capture(), any(Pageable.class));

        TransacaoFiltro filtro = filtroCaptor.getValue();
        assertEquals(TipoTransacao.GASTO, filtro.tipo());
        assertEquals(10, filtro.fkInstituicao());
        assertEquals(15, filtro.fkCategoria());
        assertEquals(100.0, filtro.valorGte());
        assertEquals(250.0, filtro.valorLte());
        assertEquals(true, filtro.parcelado());
        assertEquals(true, filtro.recorrente());
        assertEquals(LocalDateTime.of(2026, 3, 1, 0, 0), filtro.dataTransacaoGte());
        assertEquals(LocalDateTime.of(2026, 3, 31, 23, 59, 59), filtro.dataTransacaoLte());
        assertEquals("mercado", filtro.search());
    }

    @Test
    void deveLancarErroQuandoSortForInvalido() {
        TransacaoConsultaParams params = new TransacaoConsultaParams(
                1,
                10,
                "id",
                "desc",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThrows(TransacaoInvalidaException.class, () -> useCase.execute(params));
    }
}

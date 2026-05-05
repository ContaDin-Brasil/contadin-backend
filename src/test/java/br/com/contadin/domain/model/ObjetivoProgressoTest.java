package br.com.contadin.domain.model;

import br.com.contadin.domain.enums.StatusObjetivo;
import br.com.contadin.domain.enums.TipoObjetivo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObjetivoProgressoTest {

    @Test
    void deveRetornarZeroQuandoValorZero() {
        BigDecimal percentual = ObjetivoProgresso.calcularPercentual(BigDecimal.ZERO, BigDecimal.valueOf(100));

        assertEquals(BigDecimal.ZERO, percentual);
    }

    @Test
    void deveCalcularPercentualCorretamente() {
        BigDecimal percentual = ObjetivoProgresso.calcularPercentual(
                BigDecimal.valueOf(450), BigDecimal.valueOf(350));

        assertEquals(new BigDecimal("77.78"), percentual);
    }

    @Test
    void deveRetornarTranquiloQuandoGastoAbaixoDe50() {
        StatusObjetivo status = ObjetivoProgresso.calcularStatus(
                TipoObjetivo.LIMITE_GASTO, BigDecimal.valueOf(20));

        assertEquals(StatusObjetivo.TRANQUILO, status);
    }

    @Test
    void deveRetornarAtencaoQuandoGastoEntre50e74() {
        StatusObjetivo status = ObjetivoProgresso.calcularStatus(
                TipoObjetivo.LIMITE_GASTO, BigDecimal.valueOf(55));

        assertEquals(StatusObjetivo.ATENCAO, status);
    }

    @Test
    void deveRetornarCuidadoQuandoGastoEntre75e99() {
        StatusObjetivo status = ObjetivoProgresso.calcularStatus(
                TipoObjetivo.LIMITE_GASTO, BigDecimal.valueOf(78));

        assertEquals(StatusObjetivo.CUIDADO, status);
    }

    @Test
    void deveRetornarAcimaDoCombiadoQuandoGastoAtingeOuUltrapassa100() {
        StatusObjetivo status = ObjetivoProgresso.calcularStatus(
                TipoObjetivo.LIMITE_GASTO, BigDecimal.valueOf(105));

        assertEquals(StatusObjetivo.ACIMA_DO_COMBINADO, status);
    }

    @Test
    void deveRetornarNoCaminhoQuandoReceitaAbaixoDe50() {
        StatusObjetivo status = ObjetivoProgresso.calcularStatus(
                TipoObjetivo.AUMENTO_RECEITA, BigDecimal.valueOf(20));

        assertEquals(StatusObjetivo.NO_CAMINHO, status);
    }

    @Test
    void deveRetornarBomRitmoQuandoReceitaEntre50e74() {
        StatusObjetivo status = ObjetivoProgresso.calcularStatus(
                TipoObjetivo.AUMENTO_RECEITA, BigDecimal.valueOf(60));

        assertEquals(StatusObjetivo.BOM_RITMO, status);
    }

    @Test
    void deveRetornarFaltaPoucoQuandoReceitaEntre75e99() {
        StatusObjetivo status = ObjetivoProgresso.calcularStatus(
                TipoObjetivo.AUMENTO_RECEITA, BigDecimal.valueOf(80));

        assertEquals(StatusObjetivo.FALTA_POUCO, status);
    }

    @Test
    void deveRetornarMetaBatidaQuandoReceitaAtinge100() {
        StatusObjetivo status = ObjetivoProgresso.calcularStatus(
                TipoObjetivo.AUMENTO_RECEITA, BigDecimal.valueOf(100));

        assertEquals(StatusObjetivo.META_BATIDA, status);
    }

    @Test
    void deveRetornarMensagemCorretaParaCadaStatus() {
        assertEquals("Tranquilo", ObjetivoProgresso.mensagem(StatusObjetivo.TRANQUILO));
        assertEquals("Atenção", ObjetivoProgresso.mensagem(StatusObjetivo.ATENCAO));
        assertEquals("Cuidado", ObjetivoProgresso.mensagem(StatusObjetivo.CUIDADO));
        assertEquals("Acima do combinado", ObjetivoProgresso.mensagem(StatusObjetivo.ACIMA_DO_COMBINADO));
        assertEquals("No caminho", ObjetivoProgresso.mensagem(StatusObjetivo.NO_CAMINHO));
        assertEquals("Bom ritmo", ObjetivoProgresso.mensagem(StatusObjetivo.BOM_RITMO));
        assertEquals("Falta pouco", ObjetivoProgresso.mensagem(StatusObjetivo.FALTA_POUCO));
        assertEquals("Meta batida", ObjetivoProgresso.mensagem(StatusObjetivo.META_BATIDA));
    }
}

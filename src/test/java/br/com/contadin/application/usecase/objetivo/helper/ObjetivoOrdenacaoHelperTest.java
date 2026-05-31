package br.com.contadin.application.usecase.objetivo.helper;

import br.com.contadin.domain.enums.PrioridadeObjetivo;
import br.com.contadin.domain.model.Objetivo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ObjetivoOrdenacaoHelperTest {

    @Test
    @DisplayName("Construtor privado não deve ser instanciável externamente")
    void construtorPrivadoNaoDeveSerInstanciavelExternamente() throws Exception {
        Constructor<ObjetivoOrdenacaoHelper> constructor =
                ObjetivoOrdenacaoHelper.class.getDeclaredConstructor();
        assertFalse(constructor.canAccess(null));
        constructor.setAccessible(true);
        assertDoesNotThrow(() -> { constructor.newInstance(); });
    }

    @Test
    @DisplayName("porPrioridadeDesc deve ordenar ALTA → MEDIA → BAIXA → null")
    void deveOrdenarPorPrioridadeDesc() {
        Objetivo alta = Objetivo.builder().id(UUID.randomUUID()).nome("Alta").prioridade(PrioridadeObjetivo.ALTA).build();
        Objetivo media = Objetivo.builder().id(UUID.randomUUID()).nome("Media").prioridade(PrioridadeObjetivo.MEDIA).build();
        Objetivo baixa = Objetivo.builder().id(UUID.randomUUID()).nome("Baixa").prioridade(PrioridadeObjetivo.BAIXA).build();
        Objetivo nulo = Objetivo.builder().id(UUID.randomUUID()).nome("Nulo").prioridade(null).build();

        List<Objetivo> lista = Arrays.asList(nulo, baixa, media, alta);
        lista.sort(ObjetivoOrdenacaoHelper.porPrioridadeDesc());

        assertEquals("Alta", lista.get(0).getNome());
        assertEquals("Media", lista.get(1).getNome());
        assertEquals("Baixa", lista.get(2).getNome());
        assertEquals("Nulo", lista.get(3).getNome());
    }

    @Test
    @DisplayName("porPrioridadeDesc deve retornar comparador não nulo")
    void deveRetornarComparadorNaoNulo() {
        Comparator<Objetivo> comparador = ObjetivoOrdenacaoHelper.porPrioridadeDesc();
        assertNotNull(comparador);
    }
}

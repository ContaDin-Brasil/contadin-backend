package br.com.contadin.application.usecase.objetivo;

import br.com.contadin.application.exception.objetivo.ObjetivoInvalidoException;
import br.com.contadin.application.exception.objetivo.ObjetivoNaoEncontradoException;
import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.application.port.out.ObjetivoRepository;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.enums.PrioridadeObjetivo;
import br.com.contadin.domain.enums.TipoCategoria;
import br.com.contadin.domain.enums.TipoObjetivo;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.domain.model.Categoria;
import br.com.contadin.domain.model.Objetivo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AtualizarObjetivoUseCase")
class AtualizarObjetivoUseCaseTest {

    @Mock
    private ObjetivoRepository objetivoRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private AtualizarObjetivoUseCase useCase;

    private final UUID USUARIO_ID = UUID.randomUUID();
    private final UUID CATEGORIA_ID = UUID.randomUUID();
    private final LocalDate DATA_INICIO = LocalDate.of(2024, 1, 1);
    private final LocalDate DATA_FIM = LocalDate.of(2024, 12, 31);
    private final LocalDateTime CRIADO_EM = LocalDateTime.of(2024, 1, 1, 0, 0);

    private Objetivo objetivoExistente(UUID id) {
        return Objetivo.builder()
                .id(id)
                .nome("Objetivo Original")
                .descricao("Descrição original")
                .valor(new BigDecimal("1000.00"))
                .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                .prioridade(PrioridadeObjetivo.MEDIA)
                .dataInicio(DATA_INICIO)
                .dataFim(DATA_FIM)
                .fkUsuario(USUARIO_ID)
                .fkCategoria(CATEGORIA_ID)
                .criadoEm(CRIADO_EM)
                .build();
    }

    private Categoria categoriaGasto() {
        return Categoria.builder()
                .id(CATEGORIA_ID)
                .nome("Alimentação")
                .tipo(TipoCategoria.GASTO)
                .ativo(true)
                .build();
    }

    private void mockTransacaoSumZero() {
        when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(
                any(UUID.class), any(TipoTransacao.class),
                any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(BigDecimal.ZERO);
    }

    @Nested
    @DisplayName("execute() - validação de id")
    class ValidacaoId {

        @Test
        @DisplayName("id null → ObjetivoInvalidoException")
        void idNulo() {
            Objetivo patch = Objetivo.builder()
                    .nome("Novo")
                    .dataInicio(DATA_INICIO)
                    .dataFim(DATA_FIM)
                    .build();

            ObjetivoInvalidoException ex = assertThrows(ObjetivoInvalidoException.class,
                    () -> useCase.execute(null, patch));
            assertEquals("ID do objetivo é obrigatório para atualização", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("execute() - busca do objetivo")
    class BuscaObjetivo {

        @Test
        @DisplayName("objetivo não encontrado → ObjetivoNaoEncontradoException")
        void objetivoNaoEncontrado() {
            UUID id = UUID.randomUUID();
            when(objetivoRepository.findById(id)).thenReturn(Optional.empty());

            Objetivo patch = Objetivo.builder()
                    .dataInicio(DATA_INICIO)
                    .dataFim(DATA_FIM)
                    .build();

            assertThrows(ObjetivoNaoEncontradoException.class,
                    () -> useCase.execute(id, patch));
        }
    }

    @Nested
    @DisplayName("execute() - validação de datas")
    class ValidacaoDatas {

        @Test
        @DisplayName("dataFim antes de dataInicio → ObjetivoInvalidoException")
        void dataFimAntesDeDataInicio() {
            UUID id = UUID.randomUUID();
            Objetivo existente = objetivoExistente(id);
            when(objetivoRepository.findById(id)).thenReturn(Optional.of(existente));

            Objetivo patch = Objetivo.builder()
                    .dataInicio(LocalDate.of(2024, 6, 1))
                    .dataFim(LocalDate.of(2024, 1, 1))
                    .build();

            ObjetivoInvalidoException ex = assertThrows(ObjetivoInvalidoException.class,
                    () -> useCase.execute(id, patch));
            assertEquals("Data fim deve ser igual ou posterior à data início", ex.getMessage());
        }

        @Test
        @DisplayName("dataFim igual a dataInicio deve ser aceito")
        void dataFimIgualDataInicio() {
            UUID id = UUID.randomUUID();
            Objetivo existente = objetivoExistente(id);
            when(objetivoRepository.findById(id)).thenReturn(Optional.of(existente));
            when(objetivoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            mockTransacaoSumZero();

            LocalDate mesmaData = LocalDate.of(2024, 6, 1);
            Objetivo patch = Objetivo.builder()
                    .dataInicio(mesmaData)
                    .dataFim(mesmaData)
                    .build();

            assertDoesNotThrow(() -> useCase.execute(id, patch));
        }
    }

    @Nested
    @DisplayName("execute() - validação de categoria")
    class ValidacaoCategoria {

        @Test
        @DisplayName("categoria não encontrada quando fkCategoria alterada → ObjetivoInvalidoException")
        void categoriaNaoEncontrada() {
            UUID id = UUID.randomUUID();
            Objetivo existente = objetivoExistente(id);
            when(objetivoRepository.findById(id)).thenReturn(Optional.of(existente));

            UUID novaCategoria = UUID.randomUUID();
            when(categoriaRepository.findById(novaCategoria)).thenReturn(Optional.empty());

            Objetivo patch = Objetivo.builder()
                    .fkCategoria(novaCategoria)
                    .dataInicio(DATA_INICIO)
                    .dataFim(DATA_FIM)
                    .build();

            ObjetivoInvalidoException ex = assertThrows(ObjetivoInvalidoException.class,
                    () -> useCase.execute(id, patch));
            assertEquals("Categoria não encontrada", ex.getMessage());
        }

        @Test
        @DisplayName("categoria RECEITA com objetivo LIMITE_GASTO → ObjetivoInvalidoException")
        void categoriaIncompativelComTipo() {
            UUID id = UUID.randomUUID();
            Objetivo existente = objetivoExistente(id);
            when(objetivoRepository.findById(id)).thenReturn(Optional.of(existente));

            UUID novaCategoriaId = UUID.randomUUID();
            Categoria categoriaReceita = Categoria.builder()
                    .id(novaCategoriaId)
                    .tipo(TipoCategoria.RECEITA)
                    .ativo(true)
                    .build();
            when(categoriaRepository.findById(novaCategoriaId)).thenReturn(Optional.of(categoriaReceita));

            Objetivo patch = Objetivo.builder()
                    .fkCategoria(novaCategoriaId)
                    .dataInicio(DATA_INICIO)
                    .dataFim(DATA_FIM)
                    .build();

            ObjetivoInvalidoException ex = assertThrows(ObjetivoInvalidoException.class,
                    () -> useCase.execute(id, patch));
            assertTrue(ex.getMessage().contains("incompatível com objetivo"));
        }

        @Test
        @DisplayName("categoria GLOBAL deve ser compatível com qualquer tipo de objetivo")
        void categoriaGlobalDeveSerCompativel() {
            UUID id = UUID.randomUUID();
            Objetivo existente = objetivoExistente(id);
            when(objetivoRepository.findById(id)).thenReturn(Optional.of(existente));

            UUID categoriaGlobalId = UUID.randomUUID();
            Categoria categoriaGlobal = Categoria.builder()
                    .id(categoriaGlobalId)
                    .tipo(TipoCategoria.GLOBAL)
                    .ativo(true)
                    .build();
            when(categoriaRepository.findById(categoriaGlobalId)).thenReturn(Optional.of(categoriaGlobal));
            when(objetivoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            mockTransacaoSumZero();

            Objetivo patch = Objetivo.builder()
                    .fkCategoria(categoriaGlobalId)
                    .dataInicio(DATA_INICIO)
                    .dataFim(DATA_FIM)
                    .build();

            assertDoesNotThrow(() -> useCase.execute(id, patch));
        }

        @Test
        @DisplayName("não deve validar categoria quando patch não altera fkCategoria nem tipoObjetivo")
        void naoDeveValidarCategoriaQuandoPatchNaoAlteraCategoria() {
            UUID id = UUID.randomUUID();
            Objetivo existente = objetivoExistente(id);
            when(objetivoRepository.findById(id)).thenReturn(Optional.of(existente));
            when(objetivoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            mockTransacaoSumZero();

            Objetivo patch = Objetivo.builder()
                    .nome("Novo Nome")
                    .dataInicio(DATA_INICIO)
                    .dataFim(DATA_FIM)
                    .build();

            assertDoesNotThrow(() -> useCase.execute(id, patch));
            verify(categoriaRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("execute() - happy path e merge de campos")
    class HappyPath {

        @Test
        @DisplayName("deve atualizar campos fornecidos e manter os demais do existente")
        void deveAtualizarCamposEMantorExistentes() {
            UUID id = UUID.randomUUID();
            Objetivo existente = objetivoExistente(id);
            when(objetivoRepository.findById(id)).thenReturn(Optional.of(existente));
            when(objetivoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            mockTransacaoSumZero();

            Objetivo patch = Objetivo.builder()
                    .nome("Nome Atualizado")
                    .valor(new BigDecimal("2000.00"))
                    .dataInicio(DATA_INICIO)
                    .dataFim(DATA_FIM)
                    .build();

            Objetivo resultado = useCase.execute(id, patch);

            assertNotNull(resultado);
            assertEquals("Nome Atualizado", resultado.getNome());
            assertEquals(new BigDecimal("2000.00"), resultado.getValor());
            assertEquals("Descrição original", resultado.getDescricao());
        }

        @Test
        @DisplayName("deve preservar id, fkUsuario e criadoEm do existente")
        void devePreservarCamposImutaveis() {
            UUID id = UUID.randomUUID();
            Objetivo existente = objetivoExistente(id);
            when(objetivoRepository.findById(id)).thenReturn(Optional.of(existente));
            when(objetivoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            mockTransacaoSumZero();

            Objetivo patch = Objetivo.builder()
                    .nome("Patch")
                    .fkUsuario(UUID.randomUUID())
                    .criadoEm(LocalDateTime.now())
                    .dataInicio(DATA_INICIO)
                    .dataFim(DATA_FIM)
                    .build();

            Objetivo resultado = useCase.execute(id, patch);

            assertEquals(id, resultado.getId());
            assertEquals(USUARIO_ID, resultado.getFkUsuario());
            assertEquals(CRIADO_EM, resultado.getCriadoEm());
        }

        @Test
        @DisplayName("deve chamar repository.save() e retornar resultado não-nulo")
        void deveChamarSaveERetornarNaoNulo() {
            UUID id = UUID.randomUUID();
            Objetivo existente = objetivoExistente(id);
            when(objetivoRepository.findById(id)).thenReturn(Optional.of(existente));
            when(objetivoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            mockTransacaoSumZero();

            Objetivo patch = Objetivo.builder()
                    .dataInicio(DATA_INICIO)
                    .dataFim(DATA_FIM)
                    .build();

            Objetivo resultado = useCase.execute(id, patch);

            assertNotNull(resultado);
            verify(objetivoRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("deve validar categoria compatível GASTO com objetivo LIMITE_GASTO")
        void deveAceitarCategoriaGastoComLimiteGasto() {
            UUID id = UUID.randomUUID();
            Objetivo existente = objetivoExistente(id);
            when(objetivoRepository.findById(id)).thenReturn(Optional.of(existente));

            UUID novaCatId = UUID.randomUUID();
            Categoria catGasto = Categoria.builder()
                    .id(novaCatId)
                    .tipo(TipoCategoria.GASTO)
                    .ativo(true)
                    .build();
            when(categoriaRepository.findById(novaCatId)).thenReturn(Optional.of(catGasto));
            when(objetivoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            mockTransacaoSumZero();

            Objetivo patch = Objetivo.builder()
                    .fkCategoria(novaCatId)
                    .dataInicio(DATA_INICIO)
                    .dataFim(DATA_FIM)
                    .build();

            assertDoesNotThrow(() -> useCase.execute(id, patch));
        }

        @Test
        @DisplayName("deve validar categoria RECEITA compatível com objetivo AUMENTO_RECEITA")
        void deveAceitarCategoriaReceitaComAumentoReceita() {
            UUID id = UUID.randomUUID();
            Objetivo existenteReceita = Objetivo.builder()
                    .id(id)
                    .nome("Meta Receita")
                    .valor(new BigDecimal("5000.00"))
                    .tipoObjetivo(TipoObjetivo.AUMENTO_RECEITA)
                    .prioridade(PrioridadeObjetivo.ALTA)
                    .dataInicio(DATA_INICIO)
                    .dataFim(DATA_FIM)
                    .fkUsuario(USUARIO_ID)
                    .fkCategoria(CATEGORIA_ID)
                    .criadoEm(CRIADO_EM)
                    .build();
            when(objetivoRepository.findById(id)).thenReturn(Optional.of(existenteReceita));

            UUID novaCatId = UUID.randomUUID();
            Categoria catReceita = Categoria.builder()
                    .id(novaCatId)
                    .tipo(TipoCategoria.RECEITA)
                    .ativo(true)
                    .build();
            when(categoriaRepository.findById(novaCatId)).thenReturn(Optional.of(catReceita));
            when(objetivoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(transacaoRepository.sumValorByCategoriaTipoEPeriodo(
                    any(), any(), any(), any())).thenReturn(BigDecimal.ZERO);

            Objetivo patch = Objetivo.builder()
                    .fkCategoria(novaCatId)
                    .dataInicio(DATA_INICIO)
                    .dataFim(DATA_FIM)
                    .build();

            assertDoesNotThrow(() -> useCase.execute(id, patch));
        }

        @Test
        @DisplayName("deve validar categoria quando patch altera apenas tipoObjetivo")
        void deveValidarCategoriaQuandoPatchAlteraTipoObjetivo() {
            UUID id = UUID.randomUUID();
            // existente com LIMITE_GASTO e categoria GASTO
            Objetivo existente = objetivoExistente(id);
            when(objetivoRepository.findById(id)).thenReturn(Optional.of(existente));

            // categoria existente é GASTO - compatível com LIMITE_GASTO
            when(categoriaRepository.findById(CATEGORIA_ID)).thenReturn(Optional.of(categoriaGasto()));
            when(objetivoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            mockTransacaoSumZero();

            // patch só muda tipo (mantém o mesmo tipo compatível)
            Objetivo patch = Objetivo.builder()
                    .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                    .dataInicio(DATA_INICIO)
                    .dataFim(DATA_FIM)
                    .build();

            assertDoesNotThrow(() -> useCase.execute(id, patch));
            verify(categoriaRepository).findById(CATEGORIA_ID);
        }
    }

    @Nested
    @DisplayName("execute() - categoria incompatível com tipo")
    class CategoriaIncompativelComTipo {

        @Test
        @DisplayName("categoria GASTO com objetivo AUMENTO_RECEITA → ObjetivoInvalidoException")
        void categoriaGastoComAumentoReceitaDeveRejeitarr() {
            UUID id = UUID.randomUUID();
            // existente com LIMITE_GASTO, mudamos tipo para AUMENTO_RECEITA via patch
            Objetivo existente = objetivoExistente(id); // tipoObjetivo = LIMITE_GASTO
            when(objetivoRepository.findById(id)).thenReturn(Optional.of(existente));

            // nova categoria é GASTO - incompatível com AUMENTO_RECEITA
            UUID novaCatId = UUID.randomUUID();
            Categoria catGasto = Categoria.builder()
                    .id(novaCatId)
                    .tipo(TipoCategoria.GASTO)
                    .ativo(true)
                    .build();
            when(categoriaRepository.findById(novaCatId)).thenReturn(Optional.of(catGasto));

            Objetivo patch = Objetivo.builder()
                    .tipoObjetivo(TipoObjetivo.AUMENTO_RECEITA)
                    .fkCategoria(novaCatId)
                    .dataInicio(DATA_INICIO)
                    .dataFim(DATA_FIM)
                    .build();

            ObjetivoInvalidoException ex = assertThrows(ObjetivoInvalidoException.class,
                    () -> useCase.execute(id, patch));
            assertTrue(ex.getMessage().contains("incompatível com objetivo"));
        }
    }
}

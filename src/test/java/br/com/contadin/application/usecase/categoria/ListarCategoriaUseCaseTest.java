package br.com.contadin.application.usecase.categoria;

import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.domain.enums.TipoCategoria;
import br.com.contadin.domain.exception.categoria.CategoriaInvalidaException;
import br.com.contadin.domain.exception.categoria.CategoriaNaoEncontradaException;
import br.com.contadin.domain.model.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarCategoriaUseCaseTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ListarCategoriaUseCase useCase;

    private UUID fkUsuario;
    private Categoria categoria;

    @BeforeEach
    void setup() {
        fkUsuario = UUID.randomUUID();
        categoria = Categoria.builder()
                .id(UUID.randomUUID())
                .nome("Alimentação")
                .icone("fork")
                .cor("#FF5733")
                .tipo(TipoCategoria.GASTO)
                .fkUsuario(fkUsuario)
                .ativo(true)
                .build();
    }

    // -----------------------------------------------------------------------
    // execute (por tipo)
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("execute — buscar por usuário e tipo")
    class Execute {

        @Test
        @DisplayName("Deve retornar lista filtrada por usuário e tipo")
        void deveRetornarListaFiltrada() {
            List<Categoria> esperado = List.of(categoria);

            when(categoriaRepository.findByUsuario(fkUsuario, TipoCategoria.GASTO))
                    .thenReturn(esperado);

            List<Categoria> resultado = useCase.execute(fkUsuario, TipoCategoria.GASTO);

            assertSame(esperado, resultado);
            verify(categoriaRepository).findByUsuario(fkUsuario, TipoCategoria.GASTO);
        }

        @Test
        @DisplayName("Deve lançar exceção quando fkUsuario for nulo")
        void deveLancarExcecaoQuandoFkUsuarioNulo() {
            CategoriaInvalidaException exception = assertThrows(
                    CategoriaInvalidaException.class,
                    () -> useCase.execute(null, TipoCategoria.GASTO)
            );

            assertEquals("fkUsuario é obrigatório para buscar categorias.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção quando tipoCategoria for nulo")
        void deveLancarExcecaoQuandoTipoCategoraNulo() {
            CategoriaInvalidaException exception = assertThrows(
                    CategoriaInvalidaException.class,
                    () -> useCase.execute(fkUsuario, null)
            );

            assertEquals("tipoCategoria é obrigatório para buscar categorias.", exception.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // executeBuscarTodas
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("executeBuscarTodas — buscar todas sem filtro de tipo")
    class ExecuteBuscarTodas {

        @Test
        @DisplayName("Deve retornar todas as categorias do usuário")
        void deveRetornarTodasAsCategorias() {
            List<Categoria> esperado = List.of(categoria);

            when(categoriaRepository.findTodasByUsuario(fkUsuario)).thenReturn(esperado);

            List<Categoria> resultado = useCase.executeBuscarTodas(fkUsuario);

            assertSame(esperado, resultado);
            verify(categoriaRepository).findTodasByUsuario(fkUsuario);
        }

        @Test
        @DisplayName("Deve lançar exceção quando fkUsuario for nulo")
        void deveLancarExcecaoQuandoFkUsuarioNulo() {
            CategoriaInvalidaException exception = assertThrows(
                    CategoriaInvalidaException.class,
                    () -> useCase.executeBuscarTodas(null)
            );

            assertEquals("fkUsuario é obrigatório para buscar categorias.", exception.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // executeBuscarInativas
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("executeBuscarInativas — buscar categorias inativas")
    class ExecuteBuscarInativas {

        @Test
        @DisplayName("Deve retornar lista de categorias inativas do usuário")
        void deveRetornarCategoriasInativas() {
            Categoria inativa = Categoria.builder()
                    .id(UUID.randomUUID())
                    .nome("Inativa")
                    .fkUsuario(fkUsuario)
                    .ativo(false)
                    .build();

            List<Categoria> esperado = List.of(inativa);

            when(categoriaRepository.findByUsuarioInativas(fkUsuario)).thenReturn(esperado);

            List<Categoria> resultado = useCase.executeBuscarInativas(fkUsuario);

            assertSame(esperado, resultado);
            verify(categoriaRepository).findByUsuarioInativas(fkUsuario);
        }

        @Test
        @DisplayName("Deve lançar exceção quando fkUsuario for nulo")
        void deveLancarExcecaoQuandoFkUsuarioNulo() {
            CategoriaInvalidaException exception = assertThrows(
                    CategoriaInvalidaException.class,
                    () -> useCase.executeBuscarInativas(null)
            );

            assertEquals("fkUsuario é obrigatório para buscar categorias inativas.", exception.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // executeBuscarPorId
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("executeBuscarPorId — buscar por ID")
    class ExecuteBuscarPorId {

        @Test
        @DisplayName("Deve retornar a categoria quando encontrada pelo ID")
        void deveRetornarCategoriaQuandoEncontrada() {
            when(categoriaRepository.findById(categoria.getId()))
                    .thenReturn(Optional.of(categoria));

            Categoria resultado = useCase.executeBuscarPorId(categoria.getId());

            assertNotNull(resultado);
            assertEquals(categoria.getId(), resultado.getId());
            assertEquals(categoria.getNome(), resultado.getNome());
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID for nulo")
        void deveLancarExcecaoQuandoIdNulo() {
            CategoriaInvalidaException exception = assertThrows(
                    CategoriaInvalidaException.class,
                    () -> useCase.executeBuscarPorId(null)
            );

            assertEquals("ID da categoria é obrigatório para busca.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção quando categoria não for encontrada")
        void deveLancarExcecaoQuandoCategoriaNaoEncontrada() {
            UUID idInexistente = UUID.randomUUID();

            when(categoriaRepository.findById(idInexistente)).thenReturn(Optional.empty());

            CategoriaNaoEncontradaException exception = assertThrows(
                    CategoriaNaoEncontradaException.class,
                    () -> useCase.executeBuscarPorId(idInexistente)
            );

            assertEquals("Categoria não encontrada.", exception.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // executeBuscarPorNome
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("executeBuscarPorNome — buscar por nome")
    class ExecuteBuscarPorNome {

        @Test
        @DisplayName("Deve retornar lista de categorias com o nome informado")
        void deveRetornarCategoriasComNome() {
            List<Categoria> esperado = List.of(categoria);

            when(categoriaRepository.findByNomeAndUsuario("Alimentação", fkUsuario, TipoCategoria.GASTO))
                    .thenReturn(esperado);

            List<Categoria> resultado = useCase.executeBuscarPorNome("Alimentação", fkUsuario, TipoCategoria.GASTO);

            assertSame(esperado, resultado);
            verify(categoriaRepository).findByNomeAndUsuario("Alimentação", fkUsuario, TipoCategoria.GASTO);
        }

        @Test
        @DisplayName("Deve remover espaços do nome antes de buscar")
        void deveRemoverEspacosDoNome() {
            List<Categoria> esperado = List.of(categoria);

            when(categoriaRepository.findByNomeAndUsuario("Alimentação", fkUsuario, TipoCategoria.GASTO))
                    .thenReturn(esperado);

            useCase.executeBuscarPorNome("  Alimentação  ", fkUsuario, TipoCategoria.GASTO);

            verify(categoriaRepository).findByNomeAndUsuario("Alimentação", fkUsuario, TipoCategoria.GASTO);
        }

        @Test
        @DisplayName("Deve lançar exceção quando fkUsuario for nulo")
        void deveLancarExcecaoQuandoFkUsuarioNulo() {
            CategoriaInvalidaException exception = assertThrows(
                    CategoriaInvalidaException.class,
                    () -> useCase.executeBuscarPorNome("Alimentação", null, TipoCategoria.GASTO)
            );

            assertEquals("fkUsuario é obrigatório para buscar categorias por nome.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção quando tipoCategoria for nulo")
        void deveLancarExcecaoQuandoTipoCategoraNulo() {
            CategoriaInvalidaException exception = assertThrows(
                    CategoriaInvalidaException.class,
                    () -> useCase.executeBuscarPorNome("Alimentação", fkUsuario, null)
            );

            assertEquals("tipoCategoria é obrigatório para buscar categorias por nome.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção quando nome for nulo")
        void deveLancarExcecaoQuandoNomeNulo() {
            CategoriaInvalidaException exception = assertThrows(
                    CategoriaInvalidaException.class,
                    () -> useCase.executeBuscarPorNome(null, fkUsuario, TipoCategoria.GASTO)
            );

            assertEquals("Nome é obrigatório para buscar categorias por nome.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção quando nome for vazio")
        void deveLancarExcecaoQuandoNomeVazio() {
            CategoriaInvalidaException exception = assertThrows(
                    CategoriaInvalidaException.class,
                    () -> useCase.executeBuscarPorNome("   ", fkUsuario, TipoCategoria.GASTO)
            );

            assertEquals("Nome é obrigatório para buscar categorias por nome.", exception.getMessage());
        }
    }
}

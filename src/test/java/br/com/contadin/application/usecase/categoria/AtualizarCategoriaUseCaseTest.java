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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarCategoriaUseCaseTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private AtualizarCategoriaUseCase useCase;

    private UUID categoriaId;
    private Categoria categoriaExistente;

    @BeforeEach
    void setup() {
        categoriaId = UUID.randomUUID();

        categoriaExistente = Categoria.builder()
                .id(categoriaId)
                .nome("Alimentação")
                .icone("fork")
                .cor("#FF5733")
                .tipo(TipoCategoria.GASTO)
                .fkUsuario(UUID.randomUUID())
                .ativo(true)
                .build();
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve atualizar nome, icone, cor e tipo quando informados no patch")
        void deveAtualizarCamposInformados() {
            Categoria patch = Categoria.builder()
                    .nome("Transporte")
                    .icone("car")
                    .cor("#0000FF")
                    .tipo(TipoCategoria.RECEITA)
                    .build();

            when(categoriaRepository.findById(categoriaId))
                    .thenReturn(Optional.of(categoriaExistente));

            when(categoriaRepository.save(any(Categoria.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);

            useCase.execute(categoriaId, patch);

            verify(categoriaRepository).save(captor.capture());

            Categoria salva = captor.getValue();
            assertEquals("Transporte", salva.getNome());
            assertEquals("car", salva.getIcone());
            assertEquals("#0000FF", salva.getCor());
            assertEquals(TipoCategoria.RECEITA, salva.getTipo());
        }

        @Test
        @DisplayName("Deve manter valores existentes quando campos do patch forem nulos")
        void deveMantermValoresExistentesQuandoPatchForNulo() {
            Categoria patchVazio = Categoria.builder().build();

            when(categoriaRepository.findById(categoriaId))
                    .thenReturn(Optional.of(categoriaExistente));

            when(categoriaRepository.save(any(Categoria.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);

            useCase.execute(categoriaId, patchVazio);

            verify(categoriaRepository).save(captor.capture());

            Categoria salva = captor.getValue();
            assertEquals("Alimentação", salva.getNome());
            assertEquals("fork", salva.getIcone());
            assertEquals("#FF5733", salva.getCor());
            assertEquals(TipoCategoria.GASTO, salva.getTipo());
        }

        @Test
        @DisplayName("Não deve alterar fkUsuario nem ativo ao atualizar")
        void naoDeveAlterarFkUsuarioNemAtivo() {
            Categoria patch = Categoria.builder().nome("Novo Nome").build();

            when(categoriaRepository.findById(categoriaId))
                    .thenReturn(Optional.of(categoriaExistente));

            when(categoriaRepository.save(any(Categoria.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);

            useCase.execute(categoriaId, patch);

            verify(categoriaRepository).save(captor.capture());

            Categoria salva = captor.getValue();
            assertEquals(categoriaExistente.getFkUsuario(), salva.getFkUsuario());
            assertEquals(categoriaExistente.getAtivo(), salva.getAtivo());
        }

        @Test
        @DisplayName("Deve retornar a categoria salva pelo repositório")
        void deveRetornarCategoriaSalvaPeloRepositorio() {
            Categoria patch = Categoria.builder().nome("Transporte").build();
            Categoria categoriaRetornada = Categoria.builder()
                    .id(categoriaId)
                    .nome("Transporte")
                    .build();

            when(categoriaRepository.findById(categoriaId))
                    .thenReturn(Optional.of(categoriaExistente));

            when(categoriaRepository.save(any(Categoria.class)))
                    .thenReturn(categoriaRetornada);

            Categoria resultado = useCase.execute(categoriaId, patch);

            assertNotNull(resultado);
            assertEquals("Transporte", resultado.getNome());
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar CategoriaInvalidaException quando ID for nulo")
        void deveLancarExcecaoQuandoIdNulo() {
            CategoriaInvalidaException exception = assertThrows(
                    CategoriaInvalidaException.class,
                    () -> useCase.execute(null, Categoria.builder().build())
            );

            assertEquals("ID da categoria é obrigatório para atualização.", exception.getMessage());

            verify(categoriaRepository, never()).findById(any());
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar CategoriaInvalidaException quando categoria patch for nula")
        void deveLancarExcecaoQuandoCategoriaPatchNula() {
            CategoriaInvalidaException exception = assertThrows(
                    CategoriaInvalidaException.class,
                    () -> useCase.execute(categoriaId, null)
            );

            assertEquals("Dados da categoria são obrigatórios para atualização.", exception.getMessage());

            verify(categoriaRepository, never()).findById(any());
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar CategoriaNaoEncontradaException quando categoria não existir")
        void deveLancarExcecaoQuandoCategoriaNaoExistir() {
            when(categoriaRepository.findById(categoriaId))
                    .thenReturn(Optional.empty());

            CategoriaNaoEncontradaException exception = assertThrows(
                    CategoriaNaoEncontradaException.class,
                    () -> useCase.execute(categoriaId, Categoria.builder().nome("Teste").build())
            );

            assertEquals("Categoria não encontrada.", exception.getMessage());

            verify(categoriaRepository, never()).save(any());
        }
    }
}

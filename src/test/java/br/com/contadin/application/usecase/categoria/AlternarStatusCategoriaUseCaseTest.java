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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlternarStatusCategoriaUseCaseTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private AlternarStatusCategoriaUseCase useCase;

    private UUID categoriaId;
    private Categoria categoriaAtiva;
    private Categoria categoriaInativa;

    @BeforeEach
    void setup() {
        categoriaId = UUID.randomUUID();

        categoriaAtiva = Categoria.builder()
                .id(categoriaId)
                .nome("Alimentação")
                .icone("fork")
                .cor("#FF5733")
                .tipo(TipoCategoria.GASTO)
                .fkUsuario(UUID.randomUUID())
                .ativo(true)
                .build();

        categoriaInativa = Categoria.builder()
                .id(categoriaId)
                .nome("Alimentação")
                .icone("fork")
                .cor("#FF5733")
                .tipo(TipoCategoria.GASTO)
                .fkUsuario(categoriaAtiva.getFkUsuario())
                .ativo(false)
                .build();
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve desativar categoria quando estiver ativa")
        void deveDesativarCategoriaAtiva() {
            when(categoriaRepository.findByIdIncludingInactive(categoriaId))
                    .thenReturn(Optional.of(categoriaAtiva));

            ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);

            useCase.execute(categoriaId);

            verify(categoriaRepository).save(captor.capture());

            assertFalse(captor.getValue().getAtivo());
        }

        @Test
        @DisplayName("Deve ativar categoria quando estiver inativa")
        void deveAtivarCategoriaInativa() {
            when(categoriaRepository.findByIdIncludingInactive(categoriaId))
                    .thenReturn(Optional.of(categoriaInativa));

            ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);

            useCase.execute(categoriaId);

            verify(categoriaRepository).save(captor.capture());

            assertTrue(captor.getValue().getAtivo());
        }

        @Test
        @DisplayName("Deve ativar categoria quando ativo for nulo (trata como falso)")
        void deveAtivarCategoriaQuandoAtivoForNulo() {
            Categoria categoriaComAtivoNulo = Categoria.builder()
                    .id(categoriaId)
                    .nome("Alimentação")
                    .fkUsuario(UUID.randomUUID())
                    .ativo(null)
                    .build();

            when(categoriaRepository.findByIdIncludingInactive(categoriaId))
                    .thenReturn(Optional.of(categoriaComAtivoNulo));

            ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);

            useCase.execute(categoriaId);

            verify(categoriaRepository).save(captor.capture());

            assertTrue(captor.getValue().getAtivo());
        }

        @Test
        @DisplayName("Deve preservar todos os dados da categoria ao alternar status")
        void devePreservarDadosAoAlternarStatus() {
            when(categoriaRepository.findByIdIncludingInactive(categoriaId))
                    .thenReturn(Optional.of(categoriaAtiva));

            ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);

            useCase.execute(categoriaId);

            verify(categoriaRepository).save(captor.capture());

            Categoria salva = captor.getValue();
            assertEquals(categoriaAtiva.getId(), salva.getId());
            assertEquals(categoriaAtiva.getNome(), salva.getNome());
            assertEquals(categoriaAtiva.getIcone(), salva.getIcone());
            assertEquals(categoriaAtiva.getCor(), salva.getCor());
            assertEquals(categoriaAtiva.getTipo(), salva.getTipo());
            assertEquals(categoriaAtiva.getFkUsuario(), salva.getFkUsuario());
            assertFalse(salva.getAtivo());
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar CategoriaInvalidaException quando ID for nulo")
        void deveLancarExcecaoQuandoIdNulo() {
            CategoriaInvalidaException exception = assertThrows(
                    CategoriaInvalidaException.class,
                    () -> useCase.execute(null)
            );

            assertEquals("ID da categoria é obrigatório para alternar status.", exception.getMessage());

            verify(categoriaRepository, never()).findByIdIncludingInactive(any());
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar CategoriaNaoEncontradaException quando categoria não existir")
        void deveLancarExcecaoQuandoCategoriaNaoExistir() {
            when(categoriaRepository.findByIdIncludingInactive(categoriaId))
                    .thenReturn(Optional.empty());

            CategoriaNaoEncontradaException exception = assertThrows(
                    CategoriaNaoEncontradaException.class,
                    () -> useCase.execute(categoriaId)
            );

            assertEquals("Categoria não encontrada.", exception.getMessage());

            verify(categoriaRepository, never()).save(any());
        }
    }
}

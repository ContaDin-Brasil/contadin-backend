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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletarCategoriaUseCaseTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private DeletarCategoriaUseCase useCase;

    private UUID categoriaId;
    private Categoria categoria;

    @BeforeEach
    void setup() {
        categoriaId = UUID.randomUUID();

        categoria = Categoria.builder()
                .id(categoriaId)
                .nome("Alimentação")
                .tipo(TipoCategoria.GASTO)
                .fkUsuario(UUID.randomUUID())
                .ativo(true)
                .build();
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve deletar categoria ativa quando encontrada")
        void deveDeletarCategoriaAtiva() {
            when(categoriaRepository.findByIdIncludingInactive(categoriaId))
                    .thenReturn(Optional.of(categoria));

            useCase.execute(categoriaId);

            verify(categoriaRepository).deleteById(categoriaId);
        }

        @Test
        @DisplayName("Deve deletar categoria inativa quando encontrada")
        void deveDeletarCategoriaInativa() {
            Categoria categoriaInativa = Categoria.builder()
                    .id(categoriaId)
                    .nome("Alimentação")
                    .fkUsuario(UUID.randomUUID())
                    .ativo(false)
                    .build();

            when(categoriaRepository.findByIdIncludingInactive(categoriaId))
                    .thenReturn(Optional.of(categoriaInativa));

            useCase.execute(categoriaId);

            verify(categoriaRepository).deleteById(categoriaId);
        }

        @Test
        @DisplayName("Deve verificar existência antes de deletar")
        void deveVerificarExistenciaAntesDeDeletar() {
            when(categoriaRepository.findByIdIncludingInactive(categoriaId))
                    .thenReturn(Optional.of(categoria));

            useCase.execute(categoriaId);

            verify(categoriaRepository).findByIdIncludingInactive(categoriaId);
            verify(categoriaRepository).deleteById(categoriaId);
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

            assertEquals("ID da categoria é obrigatório para deleção.", exception.getMessage());

            verify(categoriaRepository, never()).findByIdIncludingInactive(any());
            verify(categoriaRepository, never()).deleteById(any());
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

            verify(categoriaRepository, never()).deleteById(any());
        }
    }
}

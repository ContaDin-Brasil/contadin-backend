package br.com.contadin.application.usecase.categoria;

import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.domain.exception.categoria.CategoriaInvalidaException;
import br.com.contadin.domain.model.Categoria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void deveListarTodasAsCategoriasPorUsuarioSemFiltroDeTipo() {
        UUID fkUsuario = UUID.randomUUID();
        List<Categoria> categorias = List.of(Categoria.builder().id(UUID.randomUUID()).build());
        when(categoriaRepository.findTodasByUsuario(fkUsuario)).thenReturn(categorias);

        List<Categoria> resultado = useCase.executeBuscarTodas(fkUsuario);

        assertSame(categorias, resultado);
        verify(categoriaRepository).findTodasByUsuario(fkUsuario);
    }

    @Test
    void deveLancarErroQuandoListarTodasComUsuarioNulo() {
        CategoriaInvalidaException exception = assertThrows(
                CategoriaInvalidaException.class,
                () -> useCase.executeBuscarTodas(null)
        );

        assertEquals("fkUsuario é obrigatório para buscar categorias.", exception.getMessage());
    }
}


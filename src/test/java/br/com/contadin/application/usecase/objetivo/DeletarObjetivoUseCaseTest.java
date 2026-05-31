package br.com.contadin.application.usecase.objetivo;

import br.com.contadin.application.exception.objetivo.ObjetivoInvalidoException;
import br.com.contadin.application.exception.objetivo.ObjetivoNaoEncontradoException;
import br.com.contadin.application.port.out.ObjetivoRepository;
import br.com.contadin.domain.model.Objetivo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeletarObjetivoUseCaseTest {

    @Mock
    private ObjetivoRepository objetivoRepository;

    @InjectMocks
    private DeletarObjetivoUseCase useCase;

    @Test
    void deveDeletarObjetivoQuandoIdValido() {
        // Arrange
        UUID id = UUID.randomUUID();
        Objetivo objetivo = Objetivo.builder().id(id).build();
        when(objetivoRepository.findById(id)).thenReturn(Optional.of(objetivo));

        // Act
        useCase.execute(id);

        // Assert
        verify(objetivoRepository).deleteById(id);
    }

    @Test
    void deveLancarErroQuandoIdNulo() {
        assertThrows(ObjetivoInvalidoException.class, () -> useCase.execute(null));
    }

    @Test
    void deveLancarErroQuandoObjetivoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(objetivoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ObjetivoNaoEncontradoException.class, () -> useCase.execute(id));
    }
}

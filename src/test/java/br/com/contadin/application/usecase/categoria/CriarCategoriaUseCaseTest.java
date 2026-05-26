package br.com.contadin.application.usecase.categoria;

import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.domain.enums.TipoCategoria;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarCategoriaUseCaseTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CriarCategoriaUseCase useCase;

    private Categoria novaCategoria;

    @BeforeEach
    void setup() {
        novaCategoria = Categoria.builder()
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
        @DisplayName("Deve criar categoria e retornar o objeto salvo")
        void deveCriarCategoriaComSucesso() {
            Categoria categoriaSalva = Categoria.builder()
                    .id(UUID.randomUUID())
                    .nome(novaCategoria.getNome())
                    .fkUsuario(novaCategoria.getFkUsuario())
                    .build();

            when(categoriaRepository.save(any(Categoria.class)))
                    .thenReturn(categoriaSalva);

            Categoria resultado = useCase.execute(novaCategoria);

            assertNotNull(resultado);
            assertEquals(categoriaSalva.getId(), resultado.getId());
        }

        @Test
        @DisplayName("Deve preservar todos os campos ao salvar")
        void devePreservarCamposAoSalvar() {
            when(categoriaRepository.save(any(Categoria.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);

            useCase.execute(novaCategoria);

            verify(categoriaRepository).save(captor.capture());

            Categoria salva = captor.getValue();
            assertEquals(novaCategoria.getNome(), salva.getNome());
            assertEquals(novaCategoria.getIcone(), salva.getIcone());
            assertEquals(novaCategoria.getCor(), salva.getCor());
            assertEquals(novaCategoria.getTipo(), salva.getTipo());
            assertEquals(novaCategoria.getFkUsuario(), salva.getFkUsuario());
            assertTrue(salva.getAtivo());
        }

        @Test
        @DisplayName("Deve definir ativo como true quando não informado")
        void deveDefinirAtivoComoTrueQuandoNaoInformado() {
            Categoria categoriasSemAtivo = Categoria.builder()
                    .nome("Transporte")
                    .tipo(TipoCategoria.GASTO)
                    .fkUsuario(UUID.randomUUID())
                    .ativo(null)
                    .build();

            when(categoriaRepository.save(any(Categoria.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);

            useCase.execute(categoriasSemAtivo);

            verify(categoriaRepository).save(captor.capture());

            assertTrue(captor.getValue().getAtivo());
        }

        @Test
        @DisplayName("Deve preservar ativo como false quando explicitamente informado")
        void devePreservarAtivoFalsoQuandoInformado() {
            Categoria categoriaInativa = Categoria.builder()
                    .nome("Transporte")
                    .tipo(TipoCategoria.GASTO)
                    .fkUsuario(UUID.randomUUID())
                    .ativo(false)
                    .build();

            when(categoriaRepository.save(any(Categoria.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);

            useCase.execute(categoriaInativa);

            verify(categoriaRepository).save(captor.capture());

            assertFalse(captor.getValue().getAtivo());
        }
    }
}

package br.com.contadin.application.usecase.usuario;

import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.domain.valueobject.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ListarUsuariosUseCase useCase;

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve retornar lista com todos os usuários")
        void deveRetornarListaComTodosOsUsuarios() {
            List<Usuario> usuarios = List.of(
                    Usuario.builder()
                            .id(UUID.randomUUID())
                            .nome("Gustavo")
                            .email(new Email("gustavo@email.com"))
                            .build(),
                    Usuario.builder()
                            .id(UUID.randomUUID())
                            .nome("Ana")
                            .email(new Email("ana@email.com"))
                            .build()
            );

            when(usuarioRepository.findAll()).thenReturn(usuarios);

            List<Usuario> resultado = useCase.execute();

            assertNotNull(resultado);
            assertEquals(2, resultado.size());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver usuários")
        void deveRetornarListaVaziaQuandoNaoHouverUsuarios() {
            when(usuarioRepository.findAll()).thenReturn(List.of());

            List<Usuario> resultado = useCase.execute();

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve delegar diretamente ao repositório sem filtros")
        void deveDelegarAoRepositorioSemFiltros() {
            when(usuarioRepository.findAll()).thenReturn(List.of());

            useCase.execute();

            verify(usuarioRepository).findAll();
        }

        @Test
        @DisplayName("Deve retornar exatamente o que o repositório retorna")
        void deveRetornarExatamenteOQueORepositorioRetorna() {
            Usuario usuario = Usuario.builder()
                    .id(UUID.randomUUID())
                    .nome("Gustavo")
                    .email(new Email("gustavo@email.com"))
                    .build();

            List<Usuario> listaEsperada = List.of(usuario);

            when(usuarioRepository.findAll()).thenReturn(listaEsperada);

            List<Usuario> resultado = useCase.execute();

            assertEquals(listaEsperada, resultado);
        }
    }
}

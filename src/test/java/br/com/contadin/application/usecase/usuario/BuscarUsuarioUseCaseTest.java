package br.com.contadin.application.usecase.usuario;

import br.com.contadin.application.exception.usuario.UsuarioNaoEncontradoException;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.domain.valueobject.Email;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private BuscarUsuarioUseCase useCase;

    private UUID usuarioId;
    private Usuario usuario;

    @BeforeEach
    void setup() {
        usuarioId = UUID.randomUUID();

        usuario = Usuario.builder()
                .id(usuarioId)
                .nome("Gustavo")
                .sobrenome("Kohatsu")
                .email(new Email("gustavo@email.com"))
                .senha("senha-hash")
                .ativo(true)
                .build();
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve retornar o usuário quando encontrado pelo ID")
        void deveRetornarUsuarioQuandoEncontrado() {
            when(usuarioRepository.findById(usuarioId))
                    .thenReturn(Optional.of(usuario));

            Usuario resultado = useCase.execute(usuarioId);

            assertNotNull(resultado);
            assertEquals(usuarioId, resultado.getId());
            assertEquals("Gustavo", resultado.getNome());
            assertEquals("Kohatsu", resultado.getSobrenome());
            assertEquals("gustavo@email.com", resultado.getEmail().valor());
        }

        @Test
        @DisplayName("Deve chamar o repositório com o ID correto")
        void deveChamarRepositorioComIdCorreto() {
            when(usuarioRepository.findById(usuarioId))
                    .thenReturn(Optional.of(usuario));

            useCase.execute(usuarioId);

            verify(usuarioRepository).findById(usuarioId);
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar UsuarioNaoEncontradoException quando usuário não existir")
        void deveLancarExcecaoQuandoUsuarioNaoExistir() {
            when(usuarioRepository.findById(usuarioId))
                    .thenReturn(Optional.empty());

            UsuarioNaoEncontradoException exception = assertThrows(
                    UsuarioNaoEncontradoException.class,
                    () -> useCase.execute(usuarioId)
            );

            assertEquals("Usuário não encontrado", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar UsuarioNaoEncontradoException para qualquer ID inexistente")
        void deveLancarExcecaoParaQualquerIdInexistente() {
            UUID idInexistente = UUID.randomUUID();

            when(usuarioRepository.findById(idInexistente))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UsuarioNaoEncontradoException.class,
                    () -> useCase.execute(idInexistente)
            );

            verify(usuarioRepository).findById(idInexistente);
        }
    }
}

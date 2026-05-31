package br.com.contadin.application.usecase.usuario;

import br.com.contadin.application.exception.usuario.UsuarioInvalidoException;
import br.com.contadin.application.exception.usuario.UsuarioNaoEncontradoException;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.domain.valueobject.Email;
import br.com.contadin.domain.valueobject.Telefone;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesativarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private DesativarUsuarioUseCase useCase;

    private UUID usuarioId;
    private Usuario usuarioAtivo;

    @BeforeEach
    void setup() {
        usuarioId = UUID.randomUUID();

        usuarioAtivo = Usuario.builder()
                .id(usuarioId)
                .nome("Gustavo")
                .sobrenome("Kohatsu")
                .email(new Email("gustavo@email.com"))
                .senha("senha-hash")
                .telefone(new Telefone("11999999999"))
                .ativo(true)
                .build();
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve desativar o usuário definindo ativo como false")
        void deveDesativarUsuario() {
            when(usuarioRepository.findById(usuarioId))
                    .thenReturn(Optional.of(usuarioAtivo));

            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);

            useCase.execute(usuarioId);

            verify(usuarioRepository).save(captor.capture());

            assertFalse(captor.getValue().isAtivo());
        }

        @Test
        @DisplayName("Deve preservar todos os demais dados do usuário ao desativar")
        void devePreservarDadosAoDesativar() {
            when(usuarioRepository.findById(usuarioId))
                    .thenReturn(Optional.of(usuarioAtivo));

            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);

            useCase.execute(usuarioId);

            verify(usuarioRepository).save(captor.capture());

            Usuario salvo = captor.getValue();

            assertEquals(usuarioAtivo.getId(), salvo.getId());
            assertEquals(usuarioAtivo.getNome(), salvo.getNome());
            assertEquals(usuarioAtivo.getSobrenome(), salvo.getSobrenome());
            assertEquals(usuarioAtivo.getEmail(), salvo.getEmail());
            assertEquals(usuarioAtivo.getSenha(), salvo.getSenha());
            assertEquals(usuarioAtivo.getTelefone(), salvo.getTelefone());
            assertFalse(salvo.isAtivo());
        }

        @Test
        @DisplayName("Deve chamar o repositório para buscar o usuário antes de desativar")
        void deveBuscarUsuarioAntesDeDesativar() {
            when(usuarioRepository.findById(usuarioId))
                    .thenReturn(Optional.of(usuarioAtivo));

            useCase.execute(usuarioId);

            verify(usuarioRepository).findById(usuarioId);
            verify(usuarioRepository).save(any(Usuario.class));
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar UsuarioInvalidoException quando ID for nulo")
        void deveLancarExcecaoQuandoIdForNulo() {
            UsuarioInvalidoException exception = assertThrows(
                    UsuarioInvalidoException.class,
                    () -> useCase.execute(null)
            );

            assertEquals("ID do usuário é obrigatório para desativação.", exception.getMessage());

            verify(usuarioRepository, never()).findById(any());
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar UsuarioNaoEncontradoException quando usuário não existir")
        void deveLancarExcecaoQuandoUsuarioNaoExistir() {
            when(usuarioRepository.findById(usuarioId))
                    .thenReturn(Optional.empty());

            UsuarioNaoEncontradoException exception = assertThrows(
                    UsuarioNaoEncontradoException.class,
                    () -> useCase.execute(usuarioId)
            );

            assertEquals("Usuário não encontrado.", exception.getMessage());

            verify(usuarioRepository, never()).save(any());
        }
    }
}

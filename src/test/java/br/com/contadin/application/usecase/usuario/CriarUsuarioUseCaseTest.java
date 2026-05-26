package br.com.contadin.application.usecase.usuario;

import br.com.contadin.application.exception.usuario.EmailJaExistenteException;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.application.port.out.security.PasswordEncoderPort;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private CriarUsuarioUseCase useCase;

    private Usuario novoUsuario;

    @BeforeEach
    void setup() {
        novoUsuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("Gustavo")
                .sobrenome("Kohatsu")
                .email(new Email("gustavo@email.com"))
                .senha("senha-plain")
                .telefone(new Telefone("11999999999"))
                .ativo(true)
                .build();
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve criar usuário com sucesso e retornar o usuário salvo")
        void deveCriarUsuarioComSucesso() {
            Usuario usuarioSalvo = Usuario.builder()
                    .id(novoUsuario.getId())
                    .nome(novoUsuario.getNome())
                    .email(novoUsuario.getEmail())
                    .senha("senha-hash")
                    .build();

            when(usuarioRepository.existsByEmail(novoUsuario.getEmail().valor()))
                    .thenReturn(false);

            when(passwordEncoderPort.encode(novoUsuario.getSenha()))
                    .thenReturn("senha-hash");

            when(usuarioRepository.save(any(Usuario.class)))
                    .thenReturn(usuarioSalvo);

            Usuario resultado = useCase.execute(novoUsuario);

            assertNotNull(resultado);
            assertEquals(novoUsuario.getId(), resultado.getId());
        }

        @Test
        @DisplayName("Deve criptografar a senha antes de salvar")
        void deveCriptografarSenhaAntesDeSalvar() {
            when(usuarioRepository.existsByEmail(novoUsuario.getEmail().valor()))
                    .thenReturn(false);

            when(passwordEncoderPort.encode(novoUsuario.getSenha()))
                    .thenReturn("senha-hash");

            when(usuarioRepository.save(any(Usuario.class)))
                    .thenReturn(novoUsuario);

            ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);

            useCase.execute(novoUsuario);

            verify(usuarioRepository).save(usuarioCaptor.capture());

            assertEquals("senha-hash", usuarioCaptor.getValue().getSenha());
        }

        @Test
        @DisplayName("Deve preservar todos os dados do usuário ao salvar")
        void devePreservarDadosDoUsuarioAoSalvar() {
            when(usuarioRepository.existsByEmail(novoUsuario.getEmail().valor()))
                    .thenReturn(false);

            when(passwordEncoderPort.encode(novoUsuario.getSenha()))
                    .thenReturn("senha-hash");

            when(usuarioRepository.save(any(Usuario.class)))
                    .thenReturn(novoUsuario);

            ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);

            useCase.execute(novoUsuario);

            verify(usuarioRepository).save(usuarioCaptor.capture());

            Usuario usuarioSalvo = usuarioCaptor.getValue();

            assertEquals(novoUsuario.getId(), usuarioSalvo.getId());
            assertEquals(novoUsuario.getNome(), usuarioSalvo.getNome());
            assertEquals(novoUsuario.getSobrenome(), usuarioSalvo.getSobrenome());
            assertEquals(novoUsuario.getEmail(), usuarioSalvo.getEmail());
            assertEquals(novoUsuario.getTelefone(), usuarioSalvo.getTelefone());
            assertEquals(novoUsuario.isAtivo(), usuarioSalvo.isAtivo());
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar EmailJaExistenteException quando e-mail já estiver cadastrado")
        void deveLancarExcecaoQuandoEmailJaExistir() {
            when(usuarioRepository.existsByEmail(novoUsuario.getEmail().valor()))
                    .thenReturn(true);

            EmailJaExistenteException exception = assertThrows(
                    EmailJaExistenteException.class,
                    () -> useCase.execute(novoUsuario)
            );

            assertEquals("E-mail já existente", exception.getMessage());

            verify(passwordEncoderPort, never()).encode(any());
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve verificar e-mail antes de qualquer outra operação")
        void deveVerificarEmailAntesDeQualquerOperacao() {
            when(usuarioRepository.existsByEmail(novoUsuario.getEmail().valor()))
                    .thenReturn(true);

            assertThrows(
                    EmailJaExistenteException.class,
                    () -> useCase.execute(novoUsuario)
            );

            verify(usuarioRepository).existsByEmail(novoUsuario.getEmail().valor());
            verify(passwordEncoderPort, never()).encode(any());
        }
    }
}

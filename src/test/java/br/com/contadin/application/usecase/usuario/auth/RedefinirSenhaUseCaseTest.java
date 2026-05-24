package br.com.contadin.application.usecase.usuario.auth;

import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.RedefinirSenhaRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.RedefinirSenhaResponse;
import br.com.contadin.application.exception.auth.PinExpiradoException;
import br.com.contadin.application.exception.auth.PinInvalidoException;
import br.com.contadin.application.exception.usuario.auth.ConfirmacaoSenhaInvalidaException;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.application.port.out.security.PasswordEncoderPort;
import br.com.contadin.application.port.out.security.TokenRecuperarSenhaRepository;
import br.com.contadin.domain.model.TokenRecuperarSenha;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedefinirSenhaUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenRecuperarSenhaRepository tokenRecuperarSenhaRepository;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private RedefinirSenhaUseCase useCase;

    private Usuario usuario;
    private TokenRecuperarSenha tokenValido;
    private RedefinirSenhaRequest request;

    @BeforeEach
    void setup() {
        UUID usuarioId = UUID.randomUUID();

        usuario = Usuario.builder()
                .id(usuarioId)
                .nome("Gustavo")
                .email(new Email("gustavo@email.com"))
                .senha("senha-atual-hash")
                .build();

        tokenValido = TokenRecuperarSenha.builder()
                .id(UUID.randomUUID())
                .token("pin-hash")
                .fkUsuario(usuarioId)
                .dataExpiracao(LocalDateTime.now().plusMinutes(10))
                .utilizado(false)
                .build();

        request = new RedefinirSenhaRequest(
                "gustavo@email.com",
                "123456",
                "nova-senha",
                "nova-senha"
        );
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve redefinir senha com sucesso e retornar mensagem de confirmação")
        void deveRedefinirSenhaComSucesso() {
            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findPrimeiroTokenValido(usuario.getId()))
                    .thenReturn(Optional.of(tokenValido));

            when(passwordEncoderPort.matches(request.pin(), tokenValido.getToken()))
                    .thenReturn(true);

            when(passwordEncoderPort.encode(request.novaSenha()))
                    .thenReturn("nova-senha-hash");

            RedefinirSenhaResponse response = useCase.execute(request);

            assertNotNull(response);
            assertEquals("Senha redefinida com sucesso.", response.mensagem());
        }

        @Test
        @DisplayName("Deve salvar usuário com a nova senha criptografada")
        void deveSalvarUsuarioComNovaSenhaCriptografada() {
            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findPrimeiroTokenValido(usuario.getId()))
                    .thenReturn(Optional.of(tokenValido));

            when(passwordEncoderPort.matches(request.pin(), tokenValido.getToken()))
                    .thenReturn(true);

            when(passwordEncoderPort.encode(request.novaSenha()))
                    .thenReturn("nova-senha-hash");

            ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);

            useCase.execute(request);

            verify(usuarioRepository).save(usuarioCaptor.capture());

            Usuario usuarioSalvo = usuarioCaptor.getValue();

            assertEquals(usuario.getId(), usuarioSalvo.getId());
            assertEquals(usuario.getNome(), usuarioSalvo.getNome());
            assertEquals(usuario.getEmail(), usuarioSalvo.getEmail());
            assertEquals("nova-senha-hash", usuarioSalvo.getSenha());
        }

        @Test
        @DisplayName("Deve marcar token como utilizado após redefinição com sucesso")
        void deveMarcarTokenComoUtilizadoAposRedefinicao() {
            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findPrimeiroTokenValido(usuario.getId()))
                    .thenReturn(Optional.of(tokenValido));

            when(passwordEncoderPort.matches(request.pin(), tokenValido.getToken()))
                    .thenReturn(true);

            when(passwordEncoderPort.encode(request.novaSenha()))
                    .thenReturn("nova-senha-hash");

            useCase.execute(request);

            verify(tokenRecuperarSenhaRepository).marcarComoUtilizado(tokenValido.getId());
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar ConfirmacaoSenhaInvalidaException quando senhas não coincidirem")
        void deveLancarExcecaoQuandoConfirmacaoSenhaNaoCoincidir() {
            RedefinirSenhaRequest requestInvalido = new RedefinirSenhaRequest(
                    "gustavo@email.com",
                    "123456",
                    "nova-senha",
                    "senha-diferente"
            );

            ConfirmacaoSenhaInvalidaException exception = assertThrows(
                    ConfirmacaoSenhaInvalidaException.class,
                    () -> useCase.execute(requestInvalido)
            );

            assertEquals("Nova senha e confirmação não coincidem.", exception.getMessage());

            verify(usuarioRepository, never()).findByEmail(anyString());
            verify(tokenRecuperarSenhaRepository, never()).findPrimeiroTokenValido(any());
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar PinInvalidoException quando usuário não for encontrado")
        void deveLancarExcecaoQuandoUsuarioNaoForEncontrado() {
            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.empty());

            PinInvalidoException exception = assertThrows(
                    PinInvalidoException.class,
                    () -> useCase.execute(request)
            );

            assertEquals("PIN incorreto, tente novamente.", exception.getMessage());

            verify(tokenRecuperarSenhaRepository, never()).findPrimeiroTokenValido(any());
            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar PinInvalidoException quando não existir token válido")
        void deveLancarExcecaoQuandoNaoExistirTokenValido() {
            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findPrimeiroTokenValido(usuario.getId()))
                    .thenReturn(Optional.empty());

            PinInvalidoException exception = assertThrows(
                    PinInvalidoException.class,
                    () -> useCase.execute(request)
            );

            assertEquals("PIN incorreto, tente novamente.", exception.getMessage());

            verify(usuarioRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar PinExpiradoException quando token estiver expirado")
        void deveLancarExcecaoQuandoTokenEstiverExpirado() {
            TokenRecuperarSenha tokenExpirado = TokenRecuperarSenha.builder()
                    .id(UUID.randomUUID())
                    .token("pin-hash")
                    .fkUsuario(usuario.getId())
                    .dataExpiracao(LocalDateTime.now().minusMinutes(1))
                    .utilizado(false)
                    .build();

            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findPrimeiroTokenValido(usuario.getId()))
                    .thenReturn(Optional.of(tokenExpirado));

            PinExpiradoException exception = assertThrows(
                    PinExpiradoException.class,
                    () -> useCase.execute(request)
            );

            assertEquals("PIN expirado. Solicite um novo PIN.", exception.getMessage());

            verify(usuarioRepository, never()).save(any());
            verify(tokenRecuperarSenhaRepository, never()).marcarComoUtilizado(any());
        }

        @Test
        @DisplayName("Deve lançar PinInvalidoException quando PIN não corresponder ao hash")
        void deveLancarExcecaoQuandoPinNaoCorresponder() {
            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findPrimeiroTokenValido(usuario.getId()))
                    .thenReturn(Optional.of(tokenValido));

            when(passwordEncoderPort.matches(request.pin(), tokenValido.getToken()))
                    .thenReturn(false);

            PinInvalidoException exception = assertThrows(
                    PinInvalidoException.class,
                    () -> useCase.execute(request)
            );

            assertEquals("PIN incorreto, tente novamente.", exception.getMessage());

            verify(usuarioRepository, never()).save(any());
            verify(tokenRecuperarSenhaRepository, never()).marcarComoUtilizado(any());
        }
    }
}

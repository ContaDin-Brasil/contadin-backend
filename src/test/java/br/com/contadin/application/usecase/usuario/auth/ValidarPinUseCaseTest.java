package br.com.contadin.application.usecase.usuario.auth;

import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ValidarPinRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ValidarPinResponse;
import br.com.contadin.application.exception.auth.PinExpiradoException;
import br.com.contadin.application.exception.auth.PinInvalidoException;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidarPinUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenRecuperarSenhaRepository tokenRecuperarSenhaRepository;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private ValidarPinUseCase useCase;

    private Usuario usuario;
    private ValidarPinRequest request;
    private TokenRecuperarSenha tokenValido;

    @BeforeEach
    void setup() {
        UUID usuarioId = UUID.randomUUID();

        usuario = Usuario.builder()
                .id(usuarioId)
                .nome("Gustavo")
                .email(new Email("gustavo@email.com"))
                .build();

        request = new ValidarPinRequest("gustavo@email.com", "123456");

        tokenValido = TokenRecuperarSenha.builder()
                .id(UUID.randomUUID())
                .token("hash-do-pin")
                .fkUsuario(usuarioId)
                .dataExpiracao(LocalDateTime.now().plusMinutes(10))
                .utilizado(false)
                .build();
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve validar PIN com sucesso e retornar mensagem de confirmação")
        void deveValidarPinComSucesso() {
            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findPrimeiroTokenValido(usuario.getId()))
                    .thenReturn(Optional.of(tokenValido));

            when(passwordEncoderPort.matches(request.pin(), tokenValido.getToken()))
                    .thenReturn(true);

            ValidarPinResponse response = useCase.execute(request);

            assertNotNull(response);
            assertEquals("PIN validado com sucesso.", response.mensagem());
        }

        @Test
        @DisplayName("Deve chamar os repositórios na ordem correta")
        void deveChamarRepositoriosNaOrdemCorreta() {
            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findPrimeiroTokenValido(usuario.getId()))
                    .thenReturn(Optional.of(tokenValido));

            when(passwordEncoderPort.matches(request.pin(), tokenValido.getToken()))
                    .thenReturn(true);

            useCase.execute(request);

            verify(usuarioRepository).findByEmail(request.email());
            verify(tokenRecuperarSenhaRepository).findPrimeiroTokenValido(usuario.getId());
            verify(passwordEncoderPort).matches(request.pin(), tokenValido.getToken());
        }

        @Test
        @DisplayName("Deve validar PIN com token prestes a expirar")
        void deveValidarPinComTokenPrestesAExpirar() {
            TokenRecuperarSenha tokenPrestesAExpirar = TokenRecuperarSenha.builder()
                    .id(UUID.randomUUID())
                    .token("hash-do-pin")
                    .fkUsuario(usuario.getId())
                    .dataExpiracao(LocalDateTime.now().plusSeconds(5))
                    .utilizado(false)
                    .build();

            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findPrimeiroTokenValido(usuario.getId()))
                    .thenReturn(Optional.of(tokenPrestesAExpirar));

            when(passwordEncoderPort.matches(request.pin(), tokenPrestesAExpirar.getToken()))
                    .thenReturn(true);

            ValidarPinResponse response = useCase.execute(request);

            assertNotNull(response);
            assertEquals("PIN validado com sucesso.", response.mensagem());
        }
    }

    @Nested
    class CasosDeErro {

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

            verify(tokenRecuperarSenhaRepository, never()).findPrimeiroTokenValido(usuario.getId());
            verify(passwordEncoderPort, never()).matches(request.pin(), tokenValido.getToken());
        }

        @Test
        @DisplayName("Deve lançar PinInvalidoException quando não existir token válido para o usuário")
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

            verify(passwordEncoderPort, never()).matches(request.pin(), tokenValido.getToken());
        }

        @Test
        @DisplayName("Deve lançar PinExpiradoException quando token estiver expirado")
        void deveLancarExcecaoQuandoTokenEstiverExpirado() {
            TokenRecuperarSenha tokenExpirado = TokenRecuperarSenha.builder()
                    .id(UUID.randomUUID())
                    .token("hash-do-pin")
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

            verify(passwordEncoderPort, never()).matches(request.pin(), tokenExpirado.getToken());
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
        }

        @Test
        @DisplayName("Deve lançar PinExpiradoException e não verificar PIN quando token expirar exatamente agora")
        void deveLancarExcecaoQuandoTokenExpirarExatamenteAgora() {
            TokenRecuperarSenha tokenExpiradoAgora = TokenRecuperarSenha.builder()
                    .id(UUID.randomUUID())
                    .token("hash-do-pin")
                    .fkUsuario(usuario.getId())
                    .dataExpiracao(LocalDateTime.now().minusNanos(1))
                    .utilizado(false)
                    .build();

            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findPrimeiroTokenValido(usuario.getId()))
                    .thenReturn(Optional.of(tokenExpiradoAgora));

            assertThrows(
                    PinExpiradoException.class,
                    () -> useCase.execute(request)
            );

            verify(passwordEncoderPort, never()).matches(request.pin(), tokenExpiradoAgora.getToken());
        }
    }
}

package br.com.contadin.application.usecase.usuario.auth;

import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ReenviarPinRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ReenviarPinResponse;
import br.com.contadin.application.exception.auth.ReenvioNaoPermitidoException;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.application.port.out.email.EmailPort;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReenviarPinUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenRecuperarSenhaRepository tokenRecuperarSenhaRepository;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private EmailPort emailPort;

    @InjectMocks
    private ReenviarPinUseCase useCase;

    private Usuario usuario;
    private ReenviarPinRequest request;

    @BeforeEach
    void setup() {
        usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("Gustavo")
                .email(new Email("gustavo@email.com"))
                .build();

        request = new ReenviarPinRequest("gustavo@email.com");
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve reenviar PIN e retornar mensagem genérica quando usuário existir sem histórico")
        void deveReenviarPinQuandoUsuarioExistirSemHistorico() {
            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findByFkUsuarioOrderByCriadoEmDesc(usuario.getId()))
                    .thenReturn(List.of());

            when(passwordEncoderPort.encode(anyString()))
                    .thenReturn("hash-pin");

            ReenviarPinResponse response = useCase.execute(request);

            assertNotNull(response);
            assertEquals("Se o e-mail estiver cadastrado, um novo PIN será enviado.", response.mensagem());
        }

        @Test
        @DisplayName("Deve retornar mensagem genérica quando usuário não existir sem realizar nenhuma operação")
        void deveRetornarMensagemGenericaQuandoUsuarioNaoExistir() {
            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.empty());

            ReenviarPinResponse response = useCase.execute(request);

            assertNotNull(response);
            assertEquals("Se o e-mail estiver cadastrado, um novo PIN será enviado.", response.mensagem());

            verify(tokenRecuperarSenhaRepository, never()).findByFkUsuarioOrderByCriadoEmDesc(any());
            verify(tokenRecuperarSenhaRepository, never()).invalidarTokensDoUsuario(any());
            verify(tokenRecuperarSenhaRepository, never()).save(any());
            verify(passwordEncoderPort, never()).encode(anyString());
            verify(emailPort, never()).enviarPinRecuperacaoSenha(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Deve reenviar PIN quando último token foi criado há mais de 60 segundos")
        void deveReenviarPinQuandoUltimoTokenForAntigo() {
            TokenRecuperarSenha tokenAntigo = TokenRecuperarSenha.builder()
                    .id(UUID.randomUUID())
                    .token("hash-antigo")
                    .fkUsuario(usuario.getId())
                    .criadoEm(LocalDateTime.now().minusSeconds(61))
                    .build();

            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findByFkUsuarioOrderByCriadoEmDesc(usuario.getId()))
                    .thenReturn(List.of(tokenAntigo));

            when(passwordEncoderPort.encode(anyString()))
                    .thenReturn("hash-pin");

            ReenviarPinResponse response = useCase.execute(request);

            assertNotNull(response);
            assertEquals("Se o e-mail estiver cadastrado, um novo PIN será enviado.", response.mensagem());

            verify(tokenRecuperarSenhaRepository).invalidarTokensDoUsuario(usuario.getId());
            verify(tokenRecuperarSenhaRepository).save(any(TokenRecuperarSenha.class));
            verify(emailPort).enviarPinRecuperacaoSenha(eq("gustavo@email.com"), eq("Gustavo"), anyString());
        }

        @Test
        @DisplayName("Deve reenviar PIN quando último token não possui data de criação")
        void deveReenviarPinQuandoUltimoTokenNaoPossuiDataCriacao() {
            TokenRecuperarSenha tokenSemData = TokenRecuperarSenha.builder()
                    .id(UUID.randomUUID())
                    .token("hash-antigo")
                    .fkUsuario(usuario.getId())
                    .criadoEm(null)
                    .build();

            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findByFkUsuarioOrderByCriadoEmDesc(usuario.getId()))
                    .thenReturn(List.of(tokenSemData));

            when(passwordEncoderPort.encode(anyString()))
                    .thenReturn("hash-pin");

            ReenviarPinResponse response = useCase.execute(request);

            assertNotNull(response);
            assertEquals("Se o e-mail estiver cadastrado, um novo PIN será enviado.", response.mensagem());
        }

        @Test
        @DisplayName("Deve invalidar tokens existentes antes de salvar o novo")
        void deveInvalidarTokensExistentesAntesDoNovo() {
            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findByFkUsuarioOrderByCriadoEmDesc(usuario.getId()))
                    .thenReturn(List.of());

            when(passwordEncoderPort.encode(anyString()))
                    .thenReturn("hash-pin");

            useCase.execute(request);

            verify(tokenRecuperarSenhaRepository).invalidarTokensDoUsuario(usuario.getId());
            verify(tokenRecuperarSenhaRepository).save(any(TokenRecuperarSenha.class));
        }

        @Test
        @DisplayName("Deve salvar token com dados corretos")
        void deveSalvarTokenComDadosCorretos() {
            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findByFkUsuarioOrderByCriadoEmDesc(usuario.getId()))
                    .thenReturn(List.of());

            when(passwordEncoderPort.encode(anyString()))
                    .thenReturn("hash-pin");

            ArgumentCaptor<TokenRecuperarSenha> tokenCaptor =
                    ArgumentCaptor.forClass(TokenRecuperarSenha.class);

            LocalDateTime antes = LocalDateTime.now();

            useCase.execute(request);

            LocalDateTime depois = LocalDateTime.now();

            verify(tokenRecuperarSenhaRepository).save(tokenCaptor.capture());

            TokenRecuperarSenha tokenSalvo = tokenCaptor.getValue();

            assertEquals("hash-pin", tokenSalvo.getToken());
            assertEquals(usuario.getId(), tokenSalvo.getFkUsuario());
            assertFalse(tokenSalvo.getUtilizado());
            assertNotNull(tokenSalvo.getDataExpiracao());
            assertTrue(tokenSalvo.getDataExpiracao().isAfter(antes.plusMinutes(9)));
            assertTrue(tokenSalvo.getDataExpiracao().isBefore(depois.plusMinutes(11)));
        }

        @Test
        @DisplayName("Deve enviar e-mail com PIN de 6 dígitos numéricos")
        void deveEnviarEmailComPinSeisDígitos() {
            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findByFkUsuarioOrderByCriadoEmDesc(usuario.getId()))
                    .thenReturn(List.of());

            when(passwordEncoderPort.encode(anyString()))
                    .thenReturn("hash-pin");

            ArgumentCaptor<String> pinCaptor = ArgumentCaptor.forClass(String.class);

            useCase.execute(request);

            verify(emailPort).enviarPinRecuperacaoSenha(
                    eq("gustavo@email.com"),
                    eq("Gustavo"),
                    pinCaptor.capture()
            );

            String pin = pinCaptor.getValue();

            assertNotNull(pin);
            assertEquals(6, pin.length());
            assertTrue(pin.matches("\\d{6}"));
        }

        @Test
        @DisplayName("Deve gerar PINs diferentes a cada execução")
        void deveGerarPinsDiferentesACadaExecucao() {
            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findByFkUsuarioOrderByCriadoEmDesc(usuario.getId()))
                    .thenReturn(List.of());

            when(passwordEncoderPort.encode(anyString()))
                    .thenReturn("hash-pin");

            ArgumentCaptor<String> pinCaptor = ArgumentCaptor.forClass(String.class);

            useCase.execute(request);
            useCase.execute(request);

            verify(emailPort, times(2)).enviarPinRecuperacaoSenha(
                    eq("gustavo@email.com"),
                    eq("Gustavo"),
                    pinCaptor.capture()
            );

            String primeiroPin = pinCaptor.getAllValues().get(0);
            String segundoPin = pinCaptor.getAllValues().get(1);

            assertNotNull(primeiroPin);
            assertNotNull(segundoPin);
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar ReenvioNaoPermitidoException quando último token foi criado há menos de 60 segundos")
        void deveLancarExcecaoQuandoUltimoTokenForRecente() {
            TokenRecuperarSenha tokenRecente = TokenRecuperarSenha.builder()
                    .id(UUID.randomUUID())
                    .token("hash-recente")
                    .fkUsuario(usuario.getId())
                    .criadoEm(LocalDateTime.now().minusSeconds(30))
                    .build();

            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findByFkUsuarioOrderByCriadoEmDesc(usuario.getId()))
                    .thenReturn(List.of(tokenRecente));

            ReenvioNaoPermitidoException exception = assertThrows(
                    ReenvioNaoPermitidoException.class,
                    () -> useCase.execute(request)
            );

            assertEquals("Aguarde 60 segundos antes de solicitar um novo PIN.", exception.getMessage());

            verify(tokenRecuperarSenhaRepository, never()).invalidarTokensDoUsuario(any());
            verify(tokenRecuperarSenhaRepository, never()).save(any());
            verify(emailPort, never()).enviarPinRecuperacaoSenha(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Deve lançar ReenvioNaoPermitidoException quando token foi criado há exatamente 1 segundo")
        void deveLancarExcecaoQuandoTokenFoiCriadoHaUmSegundo() {
            TokenRecuperarSenha tokenMuitoRecente = TokenRecuperarSenha.builder()
                    .id(UUID.randomUUID())
                    .token("hash-recente")
                    .fkUsuario(usuario.getId())
                    .criadoEm(LocalDateTime.now().minusSeconds(1))
                    .build();

            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findByFkUsuarioOrderByCriadoEmDesc(usuario.getId()))
                    .thenReturn(List.of(tokenMuitoRecente));

            assertThrows(
                    ReenvioNaoPermitidoException.class,
                    () -> useCase.execute(request)
            );
        }

        @Test
        @DisplayName("Deve considerar apenas o token mais recente do histórico para validar cooldown")
        void deveConsiderarApenasTokenMaisRecenteParaCooldown() {
            TokenRecuperarSenha tokenRecente = TokenRecuperarSenha.builder()
                    .id(UUID.randomUUID())
                    .token("hash-recente")
                    .fkUsuario(usuario.getId())
                    .criadoEm(LocalDateTime.now().minusSeconds(10))
                    .build();

            TokenRecuperarSenha tokenAntigo = TokenRecuperarSenha.builder()
                    .id(UUID.randomUUID())
                    .token("hash-antigo")
                    .fkUsuario(usuario.getId())
                    .criadoEm(LocalDateTime.now().minusHours(1))
                    .build();

            when(usuarioRepository.findByEmail(request.email()))
                    .thenReturn(Optional.of(usuario));

            when(tokenRecuperarSenhaRepository.findByFkUsuarioOrderByCriadoEmDesc(usuario.getId()))
                    .thenReturn(List.of(tokenRecente, tokenAntigo));

            assertThrows(
                    ReenvioNaoPermitidoException.class,
                    () -> useCase.execute(request)
            );
        }
    }
}

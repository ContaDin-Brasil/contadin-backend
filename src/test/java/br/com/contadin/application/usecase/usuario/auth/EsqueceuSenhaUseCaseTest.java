package br.com.contadin.application.usecase.usuario.auth;

import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.EsqueceuSenhaRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.EsqueceuSenhaResponse;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.application.port.out.email.EmailPort;
import br.com.contadin.application.port.out.security.PasswordEncoderPort;
import br.com.contadin.application.port.out.security.TokenRecuperarSenhaRepository;
import br.com.contadin.domain.model.TokenRecuperarSenha;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EsqueceuSenhaUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenRecuperarSenhaRepository tokenRecuperarSenhaRepository;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private EmailPort emailPort;

    @InjectMocks
    private EsqueceuSenhaUseCase useCase;

    private Usuario usuario;
    private EsqueceuSenhaRequest request;

    @BeforeEach
    void setup() {
        UUID usuarioId = UUID.randomUUID();

        usuario = Usuario.builder()
                .id(usuarioId)
                .nome("Gustavo")
                .email(new Email("gustavo@email.com"))
                .build();

        request = new EsqueceuSenhaRequest("gustavo@email.com");
    }

    @Test
    @DisplayName("Deve retornar mensagem genérica quando e-mail existir")
    void deveRetornarMensagemGenericaQuandoEmailExistir() {
        when(usuarioRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoderPort.encode(anyString()))
                .thenReturn("pin-hash");

        EsqueceuSenhaResponse response = useCase.execute(request);

        assertNotNull(response);
        assertEquals(
                "Se o e-mail estiver cadastrado, você receberá as instruções de recuperação de senha.",
                response.mensagem()
        );
    }

    @Test
    @DisplayName("Deve processar recuperação de senha quando usuário existir")
    void deveProcessarRecuperacaoQuandoUsuarioExistir() {
        when(usuarioRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoderPort.encode(anyString()))
                .thenReturn("pin-hash");

        useCase.execute(request);

        verify(usuarioRepository).findByEmail(request.email());

        verify(tokenRecuperarSenhaRepository)
                .invalidarTokensDoUsuario(usuario.getId());

        verify(tokenRecuperarSenhaRepository)
                .save(any(TokenRecuperarSenha.class));

        verify(emailPort)
                .enviarPinRecuperacaoSenha(
                        eq("gustavo@email.com"),
                        eq("Gustavo"),
                        anyString()
                );
    }

    @Test
    @DisplayName("Deve salvar token com dados corretos")
    void deveSalvarTokenComDadosCorretos() {
        when(usuarioRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoderPort.encode(anyString()))
                .thenReturn("pin-hash");

        ArgumentCaptor<TokenRecuperarSenha> tokenCaptor =
                ArgumentCaptor.forClass(TokenRecuperarSenha.class);

        LocalDateTime antes = LocalDateTime.now();

        useCase.execute(request);

        LocalDateTime depois = LocalDateTime.now();

        verify(tokenRecuperarSenhaRepository)
                .save(tokenCaptor.capture());

        TokenRecuperarSenha tokenSalvo = tokenCaptor.getValue();

        assertEquals("pin-hash", tokenSalvo.getToken());
        assertEquals(usuario.getId(), tokenSalvo.getFkUsuario());
        assertFalse(tokenSalvo.getUtilizado());

        assertNotNull(tokenSalvo.getDataExpiracao());

        assertTrue(
                tokenSalvo.getDataExpiracao().isAfter(antes.plusMinutes(9))
        );

        assertTrue(
                tokenSalvo.getDataExpiracao().isBefore(depois.plusMinutes(11))
        );
    }

    @Test
    @DisplayName("Deve enviar e-mail com PIN gerado")
    void deveEnviarEmailComPinGerado() {
        when(usuarioRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoderPort.encode(anyString()))
                .thenReturn("pin-hash");

        ArgumentCaptor<String> pinCaptor =
                ArgumentCaptor.forClass(String.class);

        useCase.execute(request);

        verify(emailPort).enviarPinRecuperacaoSenha(
                eq("gustavo@email.com"),
                eq("Gustavo"),
                pinCaptor.capture()
        );

        String pinGerado = pinCaptor.getValue();

        assertNotNull(pinGerado);
        assertEquals(6, pinGerado.length());
        assertTrue(pinGerado.matches("\\d{6}"));
    }

    @Test
    @DisplayName("Não deve processar recuperação quando usuário não existir")
    void naoDeveProcessarRecuperacaoQuandoUsuarioNaoExistir() {
        when(usuarioRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());

        EsqueceuSenhaResponse response = useCase.execute(request);

        assertNotNull(response);

        assertEquals(
                "Se o e-mail estiver cadastrado, você receberá as instruções de recuperação de senha.",
                response.mensagem()
        );

        verify(usuarioRepository).findByEmail(request.email());

        verify(tokenRecuperarSenhaRepository, never())
                .invalidarTokensDoUsuario(any());

        verify(tokenRecuperarSenhaRepository, never())
                .save(any());

        verify(passwordEncoderPort, never())
                .encode(anyString());

        verify(emailPort, never())
                .enviarPinRecuperacaoSenha(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve gerar PIN diferente a cada execução")
    void deveGerarPinDiferenteACadaExecucao() {
        when(usuarioRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoderPort.encode(anyString()))
                .thenReturn("pin-hash");

        ArgumentCaptor<String> pinCaptor =
                ArgumentCaptor.forClass(String.class);

        useCase.execute(request);
        useCase.execute(request);

        verify(emailPort, times(2))
                .enviarPinRecuperacaoSenha(
                        eq("gustavo@email.com"),
                        eq("Gustavo"),
                        pinCaptor.capture()
                );

        String primeiroPin = pinCaptor.getAllValues().get(0);
        String segundoPin = pinCaptor.getAllValues().get(1);

        assertNotEquals(primeiroPin, segundoPin);
    }
}
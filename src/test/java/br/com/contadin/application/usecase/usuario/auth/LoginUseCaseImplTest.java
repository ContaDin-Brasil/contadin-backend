package br.com.contadin.application.usecase.usuario.auth;

import br.com.contadin.application.dto.usuario.auth.LoginRequest;
import br.com.contadin.application.dto.usuario.auth.LoginResponse;
import br.com.contadin.application.exception.usuario.UsuarioInativoException;
import br.com.contadin.application.exception.usuario.auth.CredenciaisInvalidasException;
import br.com.contadin.application.mapper.auth.LoginMapper;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.application.port.out.security.PasswordEncoderPort;
import br.com.contadin.application.port.out.security.TokenProviderPort;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private TokenProviderPort tokenProviderPort;

    @Mock
    private LoginMapper loginMapper;

    @InjectMocks
    private LoginUseCaseImpl useCase;

    private Usuario usuario;
    private LoginRequest request;

    @BeforeEach
    void setup() {
        UUID usuarioId = UUID.randomUUID();

        usuario = Usuario.builder()
                .id(usuarioId)
                .nome("Gustavo")
                .email(new Email("gustavo@email.com"))
                .senha("senha-criptografada")
                .ativo(true)
                .build();

        request = new LoginRequest(
                "gustavo@email.com",
                "123456"
        );
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve realizar login com sucesso")
        void deveRealizarLoginComSucesso() {
            LoginResponse responseMock = mock(LoginResponse.class);

            when(usuarioRepository.findByEmail("gustavo@email.com"))
                    .thenReturn(Optional.of(usuario));

            when(passwordEncoderPort.matches(
                    request.senha(),
                    usuario.getSenha()
            )).thenReturn(true);

            when(tokenProviderPort.generateToken(usuario.getEmail().valor()))
                    .thenReturn("jwt-token");

            when(loginMapper.toResponse(usuario, "jwt-token"))
                    .thenReturn(responseMock);

            LoginResponse response = useCase.execute(request);

            assertNotNull(response);
            assertEquals(responseMock, response);

            verify(usuarioRepository)
                    .findByEmail("gustavo@email.com");

            verify(passwordEncoderPort)
                    .matches(request.senha(), usuario.getSenha());

            verify(tokenProviderPort)
                    .generateToken(usuario.getEmail().valor());

            verify(loginMapper)
                    .toResponse(usuario, "jwt-token");
        }

        @Test
        @DisplayName("Deve normalizar e-mail antes da busca")
        void deveNormalizarEmailAntesDaBusca() {
            LoginRequest requestComEspacos = new LoginRequest(
                    "  GUSTAVO@EMAIL.COM  ",
                    "123456"
            );

            LoginResponse responseMock = mock(LoginResponse.class);

            when(usuarioRepository.findByEmail("gustavo@email.com"))
                    .thenReturn(Optional.of(usuario));

            when(passwordEncoderPort.matches(anyString(), anyString()))
                    .thenReturn(true);

            when(tokenProviderPort.generateToken(anyString()))
                    .thenReturn("jwt-token");

            when(loginMapper.toResponse(any(), anyString()))
                    .thenReturn(responseMock);

            useCase.execute(requestComEspacos);

            verify(usuarioRepository)
                    .findByEmail("gustavo@email.com");
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar exceção quando usuário não existir")
        void deveLancarExcecaoQuandoUsuarioNaoExistir() {
            when(usuarioRepository.findByEmail("gustavo@email.com"))
                    .thenReturn(Optional.empty());

            CredenciaisInvalidasException exception =
                    assertThrows(
                            CredenciaisInvalidasException.class,
                            () -> useCase.execute(request)
                    );

            assertEquals(
                    "Credenciais inválidas",
                    exception.getMessage()
            );

            verify(usuarioRepository)
                    .findByEmail("gustavo@email.com");

            verify(passwordEncoderPort, never())
                    .matches(anyString(), anyString());

            verify(tokenProviderPort, never())
                    .generateToken(anyString());

            verify(loginMapper, never())
                    .toResponse(any(), anyString());
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário estiver inativo")
        void deveLancarExcecaoQuandoUsuarioInativo() {

            Usuario usuario2 = Usuario.builder()
                    .id(usuario.getId())
                    .nome(usuario.getNome())
                    .sobrenome(usuario.getSobrenome())
                    .email(usuario.getEmail())
                    .senha(usuario.getSenha())
                    .telefone(usuario.getTelefone())
                    .ativo(false)
                    .criadoEm(usuario.getCriadoEm())
                    .atualizadoEm(usuario.getAtualizadoEm())
                    .build();

            when(usuarioRepository.findByEmail("gustavo@email.com"))
                    .thenReturn(Optional.of(usuario2));

            UsuarioInativoException exception =
                    assertThrows(
                            UsuarioInativoException.class,
                            () -> useCase.execute(request)
                    );

            assertEquals(
                    "Usuário inativo",
                    exception.getMessage()
            );

            verify(usuarioRepository)
                    .findByEmail("gustavo@email.com");

            verify(passwordEncoderPort, never())
                    .matches(anyString(), anyString());

            verify(tokenProviderPort, never())
                    .generateToken(anyString());

            verify(loginMapper, never())
                    .toResponse(any(), anyString());
        }

        @Test
        @DisplayName("Deve lançar exceção quando senha estiver incorreta")
        void deveLancarExcecaoQuandoSenhaIncorreta() {
            when(usuarioRepository.findByEmail("gustavo@email.com"))
                    .thenReturn(Optional.of(usuario));

            when(passwordEncoderPort.matches(
                    request.senha(),
                    usuario.getSenha()
            )).thenReturn(false);

            CredenciaisInvalidasException exception =
                    assertThrows(
                            CredenciaisInvalidasException.class,
                            () -> useCase.execute(request)
                    );

            assertEquals(
                    "Credenciais inválidas",
                    exception.getMessage()
            );

            verify(usuarioRepository)
                    .findByEmail("gustavo@email.com");

            verify(passwordEncoderPort)
                    .matches(request.senha(), usuario.getSenha());

            verify(tokenProviderPort, never())
                    .generateToken(anyString());

            verify(loginMapper, never())
                    .toResponse(any(), anyString());
        }
    }

    @Test
    @DisplayName("Deve gerar token usando e-mail do usuário")
    void deveGerarTokenUsandoEmailDoUsuario() {
        LoginResponse responseMock = mock(LoginResponse.class);

        when(usuarioRepository.findByEmail("gustavo@email.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoderPort.matches(anyString(), anyString()))
                .thenReturn(true);

        when(tokenProviderPort.generateToken(anyString()))
                .thenReturn("jwt-token");

        when(loginMapper.toResponse(any(), anyString()))
                .thenReturn(responseMock);

        useCase.execute(request);

        verify(tokenProviderPort)
                .generateToken("gustavo@email.com");
    }
}
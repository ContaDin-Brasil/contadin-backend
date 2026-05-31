package br.com.contadin.application.usecase.usuario.auth;

import br.com.contadin.application.dto.usuario.auth.AlterarSenhaRequest;
import br.com.contadin.application.exception.usuario.UsuarioNaoEncontradoException;
import br.com.contadin.application.exception.usuario.auth.ConfirmacaoSenhaInvalidaException;
import br.com.contadin.application.exception.usuario.auth.SenhaAtualInvalidaException;
import br.com.contadin.application.exception.usuario.auth.SenhaRepetidaException;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.application.port.out.security.PasswordEncoderPort;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.domain.valueobject.Email;
import br.com.contadin.domain.valueobject.Telefone;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlterarSenhaUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private AlterarSenhaUseCase useCase;

    private UUID usuarioId;
    private Usuario usuario;
    private AlterarSenhaRequest request;

    @BeforeEach
    void setup() {

        Email email = new Email("gustavo@email.com");
        Telefone telefone = new Telefone("11999999999");
        usuarioId = UUID.randomUUID();

        usuario = Usuario.builder()
                .id(usuarioId)
                .nome("Gustavo")
                .sobrenome("Kohatsu")
                .email(email)
                .senha("senha-criptografada")
                .telefone(telefone)
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        request = new AlterarSenhaRequest(
                usuarioId,
                "senha-atual",
                "nova-senha",
                "nova-senha"
        );
    }

    @Test
    @DisplayName("Deve alterar senha com sucesso")
    void deveAlterarSenhaComSucesso() {

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoderPort.matches("senha-atual", "senha-criptografada"))
                .thenReturn(true);

        when(passwordEncoderPort.matches("nova-senha", "senha-criptografada"))
                .thenReturn(false);

        when(passwordEncoderPort.encode("nova-senha"))
                .thenReturn("nova-senha-criptografada");

        useCase.execute(request);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);

        verify(usuarioRepository).save(usuarioCaptor.capture());

        Usuario usuarioSalvo = usuarioCaptor.getValue();

        assertEquals(usuario.getId(), usuarioSalvo.getId());
        assertEquals(usuario.getNome(), usuarioSalvo.getNome());
        assertEquals(usuario.getSobrenome(), usuarioSalvo.getSobrenome());
        assertEquals(usuario.getEmail(), usuarioSalvo.getEmail());
        assertEquals("nova-senha-criptografada", usuarioSalvo.getSenha());
        assertEquals(usuario.getTelefone(), usuarioSalvo.getTelefone());
        assertEquals(usuario.isAtivo(), usuarioSalvo.isAtivo());

        verify(passwordEncoderPort).encode("nova-senha");
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não existir")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.empty());

        UsuarioNaoEncontradoException exception = assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> useCase.execute(request)
        );

        assertEquals("Usuário não encontrado", exception.getMessage());

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha atual for inválida")
    void deveLancarExcecaoQuandoSenhaAtualForInvalida() {

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoderPort.matches("senha-atual", "senha-criptografada"))
                .thenReturn(false);

        SenhaAtualInvalidaException exception = assertThrows(
                SenhaAtualInvalidaException.class,
                () -> useCase.execute(request)
        );

        assertEquals("Senha atual inválida", exception.getMessage());

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nova senha for igual à atual")
    void deveLancarExcecaoQuandoNovaSenhaForIgualAtual() {

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoderPort.matches("senha-atual", "senha-criptografada"))
                .thenReturn(true);

        when(passwordEncoderPort.matches("nova-senha", "senha-criptografada"))
                .thenReturn(true);

        SenhaRepetidaException exception = assertThrows(
                SenhaRepetidaException.class,
                () -> useCase.execute(request)
        );

        assertEquals(
                "A nova senha não pode ser igual à senha atual",
                exception.getMessage()
        );

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando confirmação da nova senha for inválida")
    void deveLancarExcecaoQuandoConfirmacaoSenhaForInvalida() {

        AlterarSenhaRequest requestInvalido = new AlterarSenhaRequest(
                usuarioId,
                "senha-atual",
                "nova-senha",
                "senha-diferente"
        );

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoderPort.matches("senha-atual", "senha-criptografada"))
                .thenReturn(true);

        when(passwordEncoderPort.matches("nova-senha", "senha-criptografada"))
                .thenReturn(false);

        ConfirmacaoSenhaInvalidaException exception = assertThrows(
                ConfirmacaoSenhaInvalidaException.class,
                () -> useCase.execute(requestInvalido)
        );

        assertEquals(
                "Confirmação da nova senha inválida",
                exception.getMessage()
        );

        verify(usuarioRepository, never()).save(any());
    }
}
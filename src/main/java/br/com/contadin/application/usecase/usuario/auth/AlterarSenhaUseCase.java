package br.com.contadin.application.usecase.usuario.auth;

import br.com.contadin.application.dto.usuario.auth.AlterarSenhaRequest;
import br.com.contadin.application.exception.usuario.UsuarioNaoEncontradoException;
import br.com.contadin.application.exception.usuario.auth.ConfirmacaoSenhaInvalidaException;
import br.com.contadin.application.exception.usuario.auth.SenhaAtualInvalidaException;
import br.com.contadin.application.exception.usuario.auth.SenhaRepetidaException;
import br.com.contadin.application.port.in.auth.AlterarSenhaInputPort;
import br.com.contadin.application.port.out.security.PasswordEncoderPort;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlterarSenhaUseCase implements AlterarSenhaInputPort {

    private static final String MSG_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado";
    private static final String MSG_SENHA_ATUAL_INVALIDA = "Senha atual inválida";
    private static final String MSG_CONFIRMACAO_INVALIDA = "Confirmação da nova senha inválida";
    private static final String MSG_SENHA_REPETIDA = "A nova senha não pode ser igual à senha atual";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    public void execute(AlterarSenhaRequest input) {

        Usuario usuario = buscarUsuario(input.id());

        validarSenhaAtual(input, usuario);
        validarNovaSenhaDiferente(input, usuario);
        validarConfirmacaoSenha(input);

        atualizarSenha(usuario, input.novaSenha());
    }

    private Usuario buscarUsuario(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(MSG_USUARIO_NAO_ENCONTRADO));
    }

    private void validarSenhaAtual(AlterarSenhaRequest input, Usuario usuario) {
        if (!passwordEncoderPort.matches(input.senhaAtual(), usuario.getSenha())) {
            throw new SenhaAtualInvalidaException(MSG_SENHA_ATUAL_INVALIDA);
        }
    }

    private void validarNovaSenhaDiferente(AlterarSenhaRequest input, Usuario usuario) {
        if (passwordEncoderPort.matches(input.novaSenha(), usuario.getSenha())) {
            throw new SenhaRepetidaException(MSG_SENHA_REPETIDA);
        }
    }

    private void validarConfirmacaoSenha(AlterarSenhaRequest input) {
        if (!input.novaSenha().equals(input.confirmacaoNovaSenha())) {
            throw new ConfirmacaoSenhaInvalidaException(MSG_CONFIRMACAO_INVALIDA);
        }
    }

    private void atualizarSenha(Usuario usuario, String novaSenha) {
        Usuario usuarioAtualizado = Usuario.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .sobrenome(usuario.getSobrenome())
                .email(usuario.getEmail())
                .senha(passwordEncoderPort.encode(novaSenha))
                .telefone(usuario.getTelefone())
                .ativo(usuario.isAtivo())
                .criadoEm(usuario.getCriadoEm())
                .atualizadoEm(usuario.getAtualizadoEm())
                .build();

        usuarioRepository.save(usuarioAtualizado);
    }
}

package br.com.contadin.application.usecase.usuario.auth;

import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.RedefinirSenhaRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.RedefinirSenhaResponse;
import br.com.contadin.application.exception.auth.PinExpiradoException;
import br.com.contadin.application.exception.auth.PinInvalidoException;
import br.com.contadin.application.exception.usuario.auth.ConfirmacaoSenhaInvalidaException;
import br.com.contadin.application.port.in.auth.RedefinirSenhaInputPort;
import br.com.contadin.application.port.out.PasswordEncoderPort;
import br.com.contadin.application.port.out.TokenRecuperarSenhaRepository;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.TokenRecuperarSenha;
import br.com.contadin.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RedefinirSenhaUseCase implements RedefinirSenhaInputPort {

    private static final String MSG_PIN_INVALIDO = "PIN incorreto, tente novamente.";
    private static final String MSG_PIN_EXPIRADO = "PIN expirado. Solicite um novo PIN.";
    private static final String MSG_CONFIRMACAO_INVALIDA = "Nova senha e confirmação não coincidem.";
    private static final String MSG_SUCESSO = "Senha redefinida com sucesso.";

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperarSenhaRepository tokenRecuperarSenhaRepository;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    @Transactional
    public RedefinirSenhaResponse execute(RedefinirSenhaRequest request) {
        validarConfirmacaoSenha(request);

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new PinInvalidoException(MSG_PIN_INVALIDO));

        TokenRecuperarSenha token = tokenRecuperarSenhaRepository
                .findPrimeiroTokenValido(usuario.getId())
                .orElseThrow(() -> new PinInvalidoException(MSG_PIN_INVALIDO));

        if (token.getDataExpiracao().isBefore(LocalDateTime.now())) {
            throw new PinExpiradoException(MSG_PIN_EXPIRADO);
        }

        if (!passwordEncoderPort.matches(request.pin(), token.getToken())) {
            throw new PinInvalidoException(MSG_PIN_INVALIDO);
        }

        atualizarSenha(usuario, request.novaSenha());
        tokenRecuperarSenhaRepository.marcarComoUtilizado(token.getId());

        return new RedefinirSenhaResponse(MSG_SUCESSO);
    }

    private void validarConfirmacaoSenha(RedefinirSenhaRequest request) {
        if (!request.novaSenha().equals(request.confirmacaoSenha())) {
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

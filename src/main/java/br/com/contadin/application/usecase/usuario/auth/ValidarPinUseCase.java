package br.com.contadin.application.usecase.usuario.auth;

import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ValidarPinRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ValidarPinResponse;
import br.com.contadin.application.exception.auth.PinExpiradoException;
import br.com.contadin.application.exception.auth.PinInvalidoException;
import br.com.contadin.application.port.in.auth.ValidarPinInputPort;
import br.com.contadin.application.port.out.security.PasswordEncoderPort;
import br.com.contadin.application.port.out.security.TokenRecuperarSenhaRepository;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.TokenRecuperarSenha;
import br.com.contadin.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ValidarPinUseCase implements ValidarPinInputPort {

    private static final String MSG_PIN_INVALIDO = "PIN incorreto, tente novamente.";
    private static final String MSG_PIN_EXPIRADO = "PIN expirado. Solicite um novo PIN.";
    private static final String MSG_SUCESSO = "PIN validado com sucesso.";

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperarSenhaRepository tokenRecuperarSenhaRepository;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    public ValidarPinResponse execute(ValidarPinRequest request) {
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

        return new ValidarPinResponse(MSG_SUCESSO);
    }
}

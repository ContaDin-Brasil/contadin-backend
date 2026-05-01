package br.com.contadin.application.usecase.usuario.auth;

import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ReenviarPinRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ReenviarPinResponse;
import br.com.contadin.application.exception.auth.ReenvioNaoPermitidoException;
import br.com.contadin.application.port.in.auth.ReenviarPinInputPort;
import br.com.contadin.application.port.out.email.EmailPort;
import br.com.contadin.application.port.out.security.PasswordEncoderPort;
import br.com.contadin.application.port.out.security.TokenRecuperarSenhaRepository;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.TokenRecuperarSenha;
import br.com.contadin.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReenviarPinUseCase implements ReenviarPinInputPort {

    private static final String MSG_GENERICA =
            "Se o e-mail estiver cadastrado, um novo PIN será enviado.";
    private static final String MSG_REENVIO_BLOQUEADO =
            "Aguarde 60 segundos antes de solicitar um novo PIN.";
    private static final int EXPIRACAO_MINUTOS = 10;
    private static final int COOLDOWN_SEGUNDOS = 60;

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperarSenhaRepository tokenRecuperarSenhaRepository;
    private final PasswordEncoderPort passwordEncoderPort;
    private final EmailPort emailPort;

    @Override
    @Transactional
    public ReenviarPinResponse execute(ReenviarPinRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.email());

        usuarioOpt.ifPresent(this::processarReenvio);

        return new ReenviarPinResponse(MSG_GENERICA);
    }

    private void processarReenvio(Usuario usuario) {
        List<TokenRecuperarSenha> historico =
                tokenRecuperarSenhaRepository.findByFkUsuarioOrderByCriadoEmDesc(usuario.getId());

        if (!historico.isEmpty()) {
            TokenRecuperarSenha ultimo = historico.get(0);
            if (ultimo.getCriadoEm() != null &&
                    ultimo.getCriadoEm().plusSeconds(COOLDOWN_SEGUNDOS).isAfter(LocalDateTime.now())) {
                throw new ReenvioNaoPermitidoException(MSG_REENVIO_BLOQUEADO);
            }
        }

        String pin = gerarPin();
        String hashPin = passwordEncoderPort.encode(pin);

        tokenRecuperarSenhaRepository.invalidarTokensDoUsuario(usuario.getId());

        TokenRecuperarSenha novoToken = TokenRecuperarSenha.builder()
                .token(hashPin)
                .dataExpiracao(LocalDateTime.now().plusMinutes(EXPIRACAO_MINUTOS))
                .fkUsuario(usuario.getId())
                .utilizado(false)
                .build();

        tokenRecuperarSenhaRepository.save(novoToken);

        emailPort.enviarPinRecuperacaoSenha(
                usuario.getEmail().valor(),
                usuario.getNome(),
                pin
        );
    }

    private String gerarPin() {
        SecureRandom random = new SecureRandom();
        int pin = 100000 + random.nextInt(900000);
        return String.valueOf(pin);
    }
}

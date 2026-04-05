package br.com.contadin.application.usecase.usuario.auth;

import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.EsqueceuSenhaRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.EsqueceuSenhaResponse;
import br.com.contadin.application.port.in.auth.EsqueceuSenhaInputPort;
import br.com.contadin.application.port.out.EmailPort;
import br.com.contadin.application.port.out.PasswordEncoderPort;
import br.com.contadin.application.port.out.TokenRecuperarSenhaRepository;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.TokenRecuperarSenha;
import br.com.contadin.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EsqueceuSenhaUseCase implements EsqueceuSenhaInputPort {

    private static final String MSG_GENERICA =
            "Se o e-mail estiver cadastrado, você receberá as instruções de recuperação de senha.";
    private static final int EXPIRACAO_MINUTOS = 10;

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperarSenhaRepository tokenRecuperarSenhaRepository;
    private final PasswordEncoderPort passwordEncoderPort;
    private final EmailPort emailPort;

    @Override
    @Transactional
    public EsqueceuSenhaResponse execute(EsqueceuSenhaRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.email());

        usuarioOpt.ifPresent(this::processarRecuperacao);

        return new EsqueceuSenhaResponse(MSG_GENERICA);
    }

    private void processarRecuperacao(Usuario usuario) {
        String pin = gerarPin();
        String hashPin = passwordEncoderPort.encode(pin);

        tokenRecuperarSenhaRepository.invalidarTokensDoUsuario(usuario.getId());

        TokenRecuperarSenha token = TokenRecuperarSenha.builder()
                .token(hashPin)
                .dataExpiracao(LocalDateTime.now().plusMinutes(EXPIRACAO_MINUTOS))
                .fkUsuario(usuario.getId())
                .utilizado(false)
                .build();

        tokenRecuperarSenhaRepository.save(token);

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

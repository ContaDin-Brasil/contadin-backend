package br.com.contadin.application.usecase.usuario.auth;

import br.com.contadin.application.dto.usuario.auth.LoginInputDTO;
import br.com.contadin.application.dto.usuario.auth.LoginOutputDTO;
import br.com.contadin.application.exception.auth.CredenciaisInvalidasException;
import br.com.contadin.application.exception.auth.UsuarioInativoException;
import br.com.contadin.application.mapper.auth.LoginMapper;
import br.com.contadin.application.port.in.auth.LoginUseCase;
import br.com.contadin.application.port.out.PasswordEncoderPort;
import br.com.contadin.application.port.out.TokenProviderPort;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCaseImpl implements LoginUseCase {

    private static final String MENSAGEM_CREDENCIAIS = "Credenciais inválidas";
    private static final String MENSAGEM_USUARIO_INATIVO = "Usuário inativo";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenProviderPort tokenProviderPort;
    private final LoginMapper loginMapper;

    @Override
    public LoginOutputDTO execute(LoginInputDTO input) {
        String emailNormalizado = input.email().trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new CredenciaisInvalidasException(MENSAGEM_CREDENCIAIS));

        if (!usuario.isAtivo()) {
            throw new UsuarioInativoException(MENSAGEM_USUARIO_INATIVO);
        }

        if (!passwordEncoderPort.matches(input.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException(MENSAGEM_CREDENCIAIS);
        }

        String token = tokenProviderPort.generateToken(usuario.getEmail().valor());
        return loginMapper.toOutput(usuario, token);
    }

}

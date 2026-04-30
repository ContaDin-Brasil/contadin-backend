package br.com.contadin.application.usecase.usuario;

import br.com.contadin.application.exception.usuario.EmailJaExistenteException;
import br.com.contadin.application.port.in.usuario.CriarUsuarioInputPort;
import br.com.contadin.application.port.out.PasswordEncoderPort;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CriarUsuarioUseCase implements CriarUsuarioInputPort {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    public Usuario execute(Usuario usuario) {

        if (usuarioRepository.existsByEmail(usuario.getEmail().valor())) {
            throw new EmailJaExistenteException("E-mail já existente");
        }

        Usuario usuarioComSenhaCriptografada = Usuario.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .sobrenome(usuario.getSobrenome())
                .email(usuario.getEmail())
                .senha(passwordEncoderPort.encode(usuario.getSenha()))
                .telefone(usuario.getTelefone())
                .ativo(usuario.isAtivo())
                .criadoEm(usuario.getCriadoEm())
                .atualizadoEm(usuario.getAtualizadoEm())
                .build();

        return usuarioRepository.save(usuarioComSenhaCriptografada);
    }
}

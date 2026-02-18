package br.com.contadin.application.usecase.usuario;

import br.com.contadin.application.exception.usuario.EmailJaExistenteException;
import br.com.contadin.application.port.in.usuario.CriarUsuarioInputPort;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CriarUsuarioUseCase implements CriarUsuarioInputPort {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Usuario execute(Usuario usuario) {

        if (usuarioRepository.existsByEmail(usuario.getEmail().valor())) {
            throw new EmailJaExistenteException("E-mail já existente");
        }

        return usuarioRepository.save(usuario);
    }
}

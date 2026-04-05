package br.com.contadin.application.usecase.usuario;

import br.com.contadin.application.exception.usuario.UsuarioNaoEncontradoException;
import br.com.contadin.application.port.in.usuario.BuscarUsuarioInputPort;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarUsuarioUseCase implements BuscarUsuarioInputPort {

    private static final String MSG_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado";
    private final UsuarioRepository usuarioRepository;

    @Override
    public Usuario execute(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(MSG_USUARIO_NAO_ENCONTRADO));
    }
}

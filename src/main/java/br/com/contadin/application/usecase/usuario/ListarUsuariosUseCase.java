package br.com.contadin.application.usecase.usuario;

import br.com.contadin.application.port.in.usuario.ListarUsuariosInputPort;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarUsuariosUseCase implements ListarUsuariosInputPort {

    private final UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> execute() {
        return usuarioRepository.findAll();
    }
}

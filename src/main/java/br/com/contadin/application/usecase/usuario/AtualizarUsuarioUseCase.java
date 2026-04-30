package br.com.contadin.application.usecase.usuario;

import br.com.contadin.application.exception.usuario.UsuarioNaoEncontradoException;
import br.com.contadin.application.port.in.usuario.AtualizarUsuarioInputPort;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtualizarUsuarioUseCase implements AtualizarUsuarioInputPort {

    private static final String MSG_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado";

    private final UsuarioRepository usuarioRepository;

    @Override
    public Usuario execute(UUID id, Usuario usuarioPatch) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(MSG_USUARIO_NAO_ENCONTRADO));

        Usuario atualizado = Usuario.builder()
                .id(existente.getId())
                .nome(usuarioPatch.getNome() != null ? usuarioPatch.getNome() : existente.getNome())
                .sobrenome(usuarioPatch.getSobrenome() != null ? usuarioPatch.getSobrenome() : existente.getSobrenome())
                .email(existente.getEmail())
                .senha(existente.getSenha())
                .telefone(usuarioPatch.getTelefone() != null ? usuarioPatch.getTelefone() : existente.getTelefone())
                .ativo(existente.isAtivo())
                .criadoEm(existente.getCriadoEm())
                .atualizadoEm(LocalDateTime.now())
                .build();

        return usuarioRepository.save(atualizado);
    }
}

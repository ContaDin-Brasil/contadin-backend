package br.com.contadin.application.usecase.usuario;

import br.com.contadin.application.exception.usuario.UsuarioInvalidoException;
import br.com.contadin.application.exception.usuario.UsuarioNaoEncontradoException;
import br.com.contadin.application.port.in.usuario.DesativarUsuarioInputPort;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DesativarUsuarioUseCase implements DesativarUsuarioInputPort {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void execute(UUID id) {
        if (id == null) {
            throw new UsuarioInvalidoException("ID do usuário é obrigatório para desativação.");
        }

        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        Usuario toSave = Usuario.builder()
                .id(existente.getId())
                .nome(existente.getNome())
                .sobrenome(existente.getSobrenome())
                .email(existente.getEmail())
                .senha(existente.getSenha())
                .telefone(existente.getTelefone())
                .ativo(false)
                .criadoEm(existente.getCriadoEm())
                .atualizadoEm(LocalDateTime.now())
                .build();

        usuarioRepository.save(toSave);
    }
}

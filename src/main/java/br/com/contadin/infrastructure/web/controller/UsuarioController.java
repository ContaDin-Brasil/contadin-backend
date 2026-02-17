package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.usuario.UsuarioPostRequest;
import br.com.contadin.application.dto.usuario.UsuarioPostResponse;
import br.com.contadin.application.port.in.usuario.CriarUsuarioInputPort;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.infrastructure.web.mapper.UsuarioWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuarioController {

    private final CriarUsuarioInputPort criarUsuarioInputPort;
    private final UsuarioWebMapper usuarioWebMapper;

    @PostMapping
    public ResponseEntity<UsuarioPostResponse> criar(
            @Valid @RequestBody UsuarioPostRequest request
    ) {
        Usuario usuario = usuarioWebMapper.toDomain(request);

        Usuario usuarioCriado = criarUsuarioInputPort.execute(usuario);

        UsuarioPostResponse response = usuarioWebMapper.toResponse(usuarioCriado);

        return ResponseEntity.status(201).body(response);
    }
}

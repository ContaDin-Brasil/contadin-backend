package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.usuario.UsuarioPostRequest;
import br.com.contadin.application.dto.usuario.UsuarioPostResponse;
import br.com.contadin.application.port.in.usuario.CriarUsuarioInputPort;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.infrastructure.web.mapper.UsuarioWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
public class UsuarioController {

    private final CriarUsuarioInputPort criarUsuarioInputPort;
    private final UsuarioWebMapper usuarioWebMapper;

    @PostMapping
    @Operation(summary = "Criar um novo usuário", description = "Cria um novo usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioPostResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content)
    })
    public ResponseEntity<UsuarioPostResponse> criar(
            @Valid @RequestBody UsuarioPostRequest request
    ) {
        Usuario usuario = usuarioWebMapper.toDomain(request);

        Usuario usuarioCriado = criarUsuarioInputPort.execute(usuario);

        UsuarioPostResponse response = usuarioWebMapper.toResponse(usuarioCriado);

        return ResponseEntity.status(201).body(response);
    }
}
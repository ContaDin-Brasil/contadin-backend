package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.usuario.UsuarioPatchRequest;
import br.com.contadin.application.dto.usuario.UsuarioResponse;
import br.com.contadin.application.port.in.usuario.AtualizarUsuarioInputPort;
import br.com.contadin.application.port.in.usuario.BuscarUsuarioInputPort;
import br.com.contadin.application.port.in.usuario.DesativarUsuarioInputPort;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.infrastructure.web.mapper.UsuarioWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
public class UsuarioController {

    private final BuscarUsuarioInputPort buscarUsuarioInputPort;
    private final AtualizarUsuarioInputPort atualizarUsuarioInputPort;
    private final DesativarUsuarioInputPort desativarUsuarioInputPort;
    private final UsuarioWebMapper usuarioWebMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por id", description = "Busca um usuário pelo seu identificador UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable UUID id) {
        Usuario usuario = buscarUsuarioInputPort.execute(id);
        UsuarioResponse response = usuarioWebMapper.toUsuarioResponse(usuario);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza dados cadastrais do usuário. Este endpoint não altera status nem credenciais.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable UUID id, @Valid @RequestBody UsuarioPatchRequest request) {
        Usuario patch = usuarioWebMapper.toDomain(request);
        Usuario atualizado = atualizarUsuarioInputPort.execute(id, patch);
        UsuarioResponse response = usuarioWebMapper.toUsuarioResponse(atualizado);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar usuário", description = "Realiza exclusão lógica de usuário, marcando-o como inativo.")
    public ResponseEntity<Void> desativarUsuario(@PathVariable UUID id) {
        desativarUsuarioInputPort.execute(id);
        return ResponseEntity.noContent().build();
    }
}
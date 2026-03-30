package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.usuario.UsuarioPostRequest;
import br.com.contadin.application.dto.usuario.UsuarioPostResponse;
import br.com.contadin.application.dto.usuario.auth.AlterarSenhaInputDTO;
import br.com.contadin.application.dto.usuario.auth.LoginInputDTO;
import br.com.contadin.application.dto.usuario.auth.LoginOutputDTO;
import br.com.contadin.application.port.in.auth.AlterarSenhaInputPort;
import br.com.contadin.application.port.in.auth.LoginUseCase;
import br.com.contadin.application.port.in.auth.LogoutInputPort;
import br.com.contadin.application.port.in.usuario.CriarUsuarioInputPort;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.infrastructure.web.mapper.UsuarioWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Login e sessão")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final LogoutInputPort logoutInputPort;
    private final CriarUsuarioInputPort criarUsuarioInputPort;
    private final AlterarSenhaInputPort alterarSenhaInputPort;
    private final UsuarioWebMapper usuarioWebMapper;

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Login", description = "Autentica por e-mail e senha e retorna dados básicos do usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginOutputDTO.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content)
    })
    public ResponseEntity<LoginOutputDTO> login(@Valid @RequestBody LoginInputDTO request) {
        LoginOutputDTO output = loginUseCase.execute(request);
        return ResponseEntity.ok(output);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revoga o token JWT atual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logout realizado com sucesso", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content)
    })
    public ResponseEntity<Void> logout(@RequestHeader(name = "Authorization", required = false) String authorizationHeader) {
        logoutInputPort.execute(authorizationHeader);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cadastro")
    @SecurityRequirements
    @Operation(summary = "Cadastro de usuário", description = "Cria um novo usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioPostResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "E-mail já existente",
                    content = @Content)
    })
    public ResponseEntity<UsuarioPostResponse> cadastrar(@Valid @RequestBody UsuarioPostRequest request) {
        Usuario usuario = usuarioWebMapper.toDomain(request);
        Usuario usuarioCriado = criarUsuarioInputPort.execute(usuario);
        UsuarioPostResponse response = usuarioWebMapper.toResponse(usuarioCriado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/senha")
    @Operation(summary = "Alterar senha", description = "Altera a senha do usuário validando senha atual e confirmação da nova senha.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso", content = @Content),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou confirmação incorreta", content = @Content),
            @ApiResponse(responseCode = "401", description = "Senha atual inválida", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<Void> alterarSenha(@Valid @RequestBody AlterarSenhaInputDTO request) {
        alterarSenhaInputPort.execute(request);
        return ResponseEntity.noContent().build();
    }
}

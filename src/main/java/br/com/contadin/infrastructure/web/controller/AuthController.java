package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.EsqueceuSenhaRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.EsqueceuSenhaResponse;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ReenviarPinRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ReenviarPinResponse;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.RedefinirSenhaRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.RedefinirSenhaResponse;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ValidarPinRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ValidarPinResponse;
import br.com.contadin.application.dto.usuario.CriarUsuarioRequest;
import br.com.contadin.application.dto.usuario.CriarUsuarioResponse;
import br.com.contadin.application.dto.usuario.auth.AlterarSenhaRequest;
import br.com.contadin.application.dto.usuario.auth.LoginRequest;
import br.com.contadin.application.dto.usuario.auth.LoginResponse;
import br.com.contadin.application.port.in.auth.AlterarSenhaInputPort;
import br.com.contadin.application.port.in.auth.EsqueceuSenhaInputPort;
import br.com.contadin.application.port.in.auth.LoginUseCase;
import br.com.contadin.application.port.in.auth.LogoutInputPort;
import br.com.contadin.application.port.in.auth.ReenviarPinInputPort;
import br.com.contadin.application.port.in.auth.RedefinirSenhaInputPort;
import br.com.contadin.application.port.in.auth.ValidarPinInputPort;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Login, sessão e recuperação de senha")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final LogoutInputPort logoutInputPort;
    private final CriarUsuarioInputPort criarUsuarioInputPort;
    private final AlterarSenhaInputPort alterarSenhaInputPort;
    private final EsqueceuSenhaInputPort esqueceuSenhaInputPort;
    private final ValidarPinInputPort validarPinInputPort;
    private final RedefinirSenhaInputPort redefinirSenhaInputPort;
    private final ReenviarPinInputPort reenviarPinInputPort;
    private final UsuarioWebMapper usuarioWebMapper;

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Login", description = "Autentica por e-mail e senha e retorna dados básicos do usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido", content = @Content),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content)
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse output = loginUseCase.execute(request);
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
                            schema = @Schema(implementation = CriarUsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content),
            @ApiResponse(responseCode = "409", description = "E-mail já existente", content = @Content)
    })
    public ResponseEntity<CriarUsuarioResponse> cadastrar(@Valid @RequestBody CriarUsuarioRequest request) {
        Usuario usuario = usuarioWebMapper.toDomain(request);
        Usuario usuarioCriado = criarUsuarioInputPort.execute(usuario);
        CriarUsuarioResponse response = usuarioWebMapper.toResponse(usuarioCriado);
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
    public ResponseEntity<Void> alterarSenha(@Valid @RequestBody AlterarSenhaRequest request) {
        alterarSenhaInputPort.execute(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/esqueceu-senha")
    @SecurityRequirements
    @Operation(summary = "Solicitar recuperação de senha",
            description = "Envia um PIN de 6 dígitos para o e-mail cadastrado. Sempre retorna mensagem genérica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação processada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EsqueceuSenhaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido", content = @Content)
    })
    public ResponseEntity<EsqueceuSenhaResponse> esqueceuSenha(@Valid @RequestBody EsqueceuSenhaRequest request) {
        EsqueceuSenhaResponse response = esqueceuSenhaInputPort.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validar-pin")
    @SecurityRequirements
    @Operation(summary = "Validar PIN", description = "Valida o PIN de recuperação de senha enviado por e-mail.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PIN válido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidarPinResponse.class))),
            @ApiResponse(responseCode = "400", description = "PIN inválido ou expirado", content = @Content)
    })
    public ResponseEntity<ValidarPinResponse> validarPin(@Valid @RequestBody ValidarPinRequest request) {
        ValidarPinResponse response = validarPinInputPort.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/redefinir-senha")
    @SecurityRequirements
    @Operation(summary = "Redefinir senha", description = "Redefine a senha do usuário após validação do PIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RedefinirSenhaResponse.class))),
            @ApiResponse(responseCode = "400", description = "PIN inválido, expirado ou senhas não conferem", content = @Content)
    })
    public ResponseEntity<RedefinirSenhaResponse> redefinirSenha(@Valid @RequestBody RedefinirSenhaRequest request) {
        RedefinirSenhaResponse response = redefinirSenhaInputPort.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reenviar-pin")
    @SecurityRequirements
    @Operation(summary = "Reenviar PIN", description = "Gera e reenvia um novo PIN de recuperação de senha.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação processada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReenviarPinResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido", content = @Content),
            @ApiResponse(responseCode = "429", description = "Aguarde 60 segundos antes de solicitar novo PIN", content = @Content)
    })
    public ResponseEntity<ReenviarPinResponse> reenviarPin(@Valid @RequestBody ReenviarPinRequest request) {
        ReenviarPinResponse response = reenviarPinInputPort.execute(request);
        return ResponseEntity.ok(response);
    }
}

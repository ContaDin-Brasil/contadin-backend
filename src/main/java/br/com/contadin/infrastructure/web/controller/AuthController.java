package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.usuario.auth.LoginInputDTO;
import br.com.contadin.application.dto.usuario.auth.LoginOutputDTO;
import br.com.contadin.application.port.in.auth.LoginUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}

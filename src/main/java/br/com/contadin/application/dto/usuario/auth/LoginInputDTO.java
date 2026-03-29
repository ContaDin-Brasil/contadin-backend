package br.com.contadin.application.dto.usuario.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginInputDTO(

        @Schema(example = "sysadmin@contadin.com")
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @Schema(example = "sysadmin")
        @NotBlank(message = "Senha é obrigatória")
        String senha

) {
}

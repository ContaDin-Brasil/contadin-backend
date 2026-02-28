package br.com.contadin.application.dto.tokenRecuperarSenha;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TokenRecuperarSenhaRequest(
        @NotNull(message = "Id é obrigatório")
        Integer id,

        @NotBlank(message = "Token é obrigatório")
        String token,

        @NotNull(message = "Data de expiração é obrigatória")
        LocalDateTime dataExpiracao,

        @NotNull(message = "Fk usuário é obrigatório")
        Integer fkUsuario
) {
}

package br.com.contadin.application.dto.tokenRecuperarSenha;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TokenRecuperarSenhaResponse(
        @NotNull(message = "Id é obrigatório")
        Integer id,

        @NotBlank(message = "Token é obrigatório")
        String token,

        @NotNull(message = "Data de expiração é obrigatória")
        LocalDateTime dataExpiracao,

        @NotNull(message = "Fk usuário é obrigatório")
        Integer fkUsuario,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime criadoEm,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime atualizadoEm
) {
}

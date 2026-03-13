package br.com.contadin.application.dto.tokenRecuperarSenha;

import jakarta.validation.constraints.NotBlank;

public record TokenRecuperarSenhaRequest(
        @NotBlank(message = "Token é obrigatório")
        String token
) {
}

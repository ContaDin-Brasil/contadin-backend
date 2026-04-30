package br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RedefinirSenhaRequest(
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "PIN é obrigatório")
        String pin,

        @NotBlank(message = "Nova senha é obrigatória")
        String novaSenha,

        @NotBlank(message = "Confirmação de senha é obrigatória")
        String confirmacaoSenha
) {}

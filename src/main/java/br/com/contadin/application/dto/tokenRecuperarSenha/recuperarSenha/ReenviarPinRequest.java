package br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ReenviarPinRequest(
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email
) {}

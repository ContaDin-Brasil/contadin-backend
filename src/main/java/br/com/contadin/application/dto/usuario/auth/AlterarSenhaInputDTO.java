package br.com.contadin.application.dto.usuario.auth;

import br.com.contadin.application.validation.SenhaForte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AlterarSenhaInputDTO(
        @NotNull(message = "Id do usuário é obrigatório")
        UUID id,

        @NotBlank(message = "Senha atual é obrigatória")
        String senhaAtual,

        @NotBlank(message = "Nova senha é obrigatória")
        @SenhaForte(message = """
                Senha deve conter no mínimo 8 caracteres,
                um número,
                um caractere especial e
                não conter sequências
                ou repetições numéricas
                """)
        String novaSenha,

        @NotBlank(message = "Confirmação da nova senha é obrigatória")
        String confirmacaoNovaSenha
) {
}

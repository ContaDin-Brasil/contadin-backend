package br.com.contadin.application.dto.usuario;

import br.com.contadin.application.validation.SenhaForte;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarUsuarioRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Schema(example = "Sys")
        String nome,

        @Schema(example = "Admin")
        String sobrenome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Schema(example = "sysadmin@sptech.school")
        String email,

        String telefone,

        @NotBlank(message = "Senha é obrigatória")
        @SenhaForte(message = """
                Senha deve conter no mínimo 8 caracteres,
                um número,
                um caractere especial e
                não conter sequências
                ou repetições numéricas
                """)
        String senha,

        @NotNull(message = "Campo ativo é obrigatório")
        Boolean ativo
) {}

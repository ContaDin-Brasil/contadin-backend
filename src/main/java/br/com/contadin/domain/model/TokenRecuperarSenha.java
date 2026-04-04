package br.com.contadin.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class TokenRecuperarSenha {
    private Integer id;
    private String token;
    private LocalDateTime dataExpiracao;
    private UUID fkUsuario;
    private Boolean utilizado;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}

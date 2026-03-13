package br.com.contadin.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TokenRecuperarSenha {
    private Integer id;
    private String token;
    private LocalDateTime dataExpiracao;
    private Integer fkUsuario;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}

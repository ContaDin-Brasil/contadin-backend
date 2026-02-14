package br.com.contadin.domain.model;

import java.time.LocalTime;

public class TokenRecuperarSenha {
    private Integer id;
    private String value;
    private LocalTime dataExpiracao;
    private Integer fkUsuario;
}

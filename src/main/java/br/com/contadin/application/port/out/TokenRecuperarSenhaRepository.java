package br.com.contadin.application.port.out;

import br.com.contadin.domain.model.TokenRecuperarSenha;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRecuperarSenhaRepository {

    TokenRecuperarSenha save(TokenRecuperarSenha tokenRecuperarSenha);

    Optional<TokenRecuperarSenha> findPrimeiroTokenValido(UUID fkUsuario);

    List<TokenRecuperarSenha> findByFkUsuarioOrderByCriadoEmDesc(UUID fkUsuario);

    void invalidarTokensDoUsuario(UUID fkUsuario);

    void marcarComoUtilizado(UUID id);
}

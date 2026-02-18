package br.com.contadin.infrastructure.persistence.repository.tokenRecuperarSenha;

import br.com.contadin.application.port.out.TokenRecuperarSenhaRepository;
import br.com.contadin.domain.model.TokenRecuperarSenha;
import br.com.contadin.infrastructure.persistence.entity.TokenRecuperarSenhaEntity;
import br.com.contadin.infrastructure.persistence.entity.UsuarioEntity;
import br.com.contadin.infrastructure.persistence.mapper.TokenRecuperarSenhaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TokenRecuperarSenhaRepositoryImplementations implements TokenRecuperarSenhaRepository {

    private final TokenRecuperarSenhaJpaRepository jpaRepository;
    private final TokenRecuperarSenhaPersistenceMapper persistenceMapper;

    @Override
    public TokenRecuperarSenha save(TokenRecuperarSenha tokenRecuperarSenha) {
        TokenRecuperarSenhaEntity entity = persistenceMapper.toEntity(tokenRecuperarSenha);
        //

        return null;
    }
}

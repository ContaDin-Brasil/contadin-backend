package br.com.contadin.infrastructure.persistence.repository.tokenRecuperarSenha;

import br.com.contadin.application.port.out.security.TokenRecuperarSenhaRepository;
import br.com.contadin.domain.model.TokenRecuperarSenha;
import br.com.contadin.infrastructure.persistence.entity.TokenRecuperarSenhaEntity;
import br.com.contadin.infrastructure.persistence.mapper.TokenRecuperarSenhaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TokenRecuperarSenhaRepositoryImplementations implements TokenRecuperarSenhaRepository {

    private final TokenRecuperarSenhaJpaRepository jpaRepository;
    private final TokenRecuperarSenhaPersistenceMapper persistenceMapper;

    @Override
    public TokenRecuperarSenha save(TokenRecuperarSenha tokenRecuperarSenha) {
        TokenRecuperarSenhaEntity entity = persistenceMapper.toEntity(tokenRecuperarSenha);
        TokenRecuperarSenhaEntity saved = jpaRepository.save(entity);
        return persistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<TokenRecuperarSenha> findPrimeiroTokenValido(UUID fkUsuario) {
        return jpaRepository.findFirstByFkUsuarioAndUtilizadoFalseOrderByCriadoEmDesc(fkUsuario)
                .map(persistenceMapper::toDomain);
    }

    @Override
    public List<TokenRecuperarSenha> findByFkUsuarioOrderByCriadoEmDesc(UUID fkUsuario) {
        return jpaRepository.findByFkUsuarioOrderByCriadoEmDesc(fkUsuario)
                .stream()
                .map(persistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void invalidarTokensDoUsuario(UUID fkUsuario) {
        jpaRepository.invalidarTokensDoUsuario(fkUsuario);
    }

    @Override
    public void marcarComoUtilizado(UUID id) {
        jpaRepository.marcarComoUtilizado(id);
    }
}

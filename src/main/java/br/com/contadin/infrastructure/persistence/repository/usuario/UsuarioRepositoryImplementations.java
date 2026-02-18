package br.com.contadin.infrastructure.persistence.repository.usuario;

import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.infrastructure.persistence.entity.UsuarioEntity;
import br.com.contadin.infrastructure.persistence.mapper.UsuarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UsuarioRepositoryImplementations implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioPersistenceMapper mapper;

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = mapper.toEntity(usuario);
        UsuarioEntity saved = jpaRepository.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}

package br.com.contadin.infrastructure.persistence.repository.categoria;

import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.domain.model.Categoria;
import br.com.contadin.infrastructure.persistence.entity.CategoriaEntity;
import br.com.contadin.infrastructure.persistence.mapper.CategoriaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoriaRepositoryImplementations implements CategoriaRepository {

    private final CategoriaJpaRepository jpaRepository;
    private final CategoriaPersistenceMapper mapper;

    @Override
    public Categoria save(Categoria categoria) {
        CategoriaEntity entity = mapper.toEntity(categoria);
        jpaRepository.save(entity);
        return mapper.toDomain(entity);
    }
}
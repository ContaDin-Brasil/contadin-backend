package br.com.contadin.infrastructure.persistence.repository;

import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.domain.model.Categoria;
import br.com.contadin.infrastructure.persistence.mapper.CategoriaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoriaRepositoryImplementations implements CategoriaRepository {
    private final CategoriaJpaRepository jpaRepository;
    private final CategoriaMapper mapper;

    @Override
    public Categoria save(Categoria categoria) {
        var entity = mapper.toEntity(categoria);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
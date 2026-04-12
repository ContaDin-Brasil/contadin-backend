package br.com.contadin.infrastructure.persistence.repository.categoria;

import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.domain.enums.TipoCategoria;
import br.com.contadin.domain.model.Categoria;
import br.com.contadin.infrastructure.persistence.entity.CategoriaEntity;
import br.com.contadin.infrastructure.persistence.mapper.CategoriaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CategoriaRepositoryImplementations implements CategoriaRepository {

    private final CategoriaJpaRepository jpaRepository;
    private final CategoriaPersistenceMapper mapper;

    @Override
    public Categoria save(Categoria categoria) {
        CategoriaEntity entity = mapper.toEntity(categoria);
        CategoriaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Categoria> findById(UUID id) {
        return jpaRepository.findByIdAndAtivoTrue(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Categoria> findByIdIncludingInactive(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Categoria> findByUsuario(UUID fkUsuario, TipoCategoria tipoCategoria) {
        return jpaRepository.findByFkUsuarioAndTipoIncludingGlobalAndAtivoTrue(fkUsuario, tipoCategoria)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Categoria> findByUsuarioInativas(UUID fkUsuario) {
        return jpaRepository.findByFkUsuarioAndAtivoFalse(fkUsuario)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Categoria> findByNomeAndUsuario(String nome, UUID fkUsuario, TipoCategoria tipoCategoria) {
        return jpaRepository.findByFkUsuarioAndNomeContainingIgnoreCaseAndTipoIncludingGlobalAndAtivoTrue(
                        fkUsuario,
                        nome,
                        tipoCategoria
                )
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
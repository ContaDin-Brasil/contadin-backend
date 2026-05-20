package br.com.contadin.infrastructure.persistence.repository.objetivo;

import br.com.contadin.application.port.out.ObjetivoRepository;
import br.com.contadin.domain.model.Objetivo;
import br.com.contadin.infrastructure.persistence.mapper.ObjetivoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ObjetivoRepositoryImplementations implements ObjetivoRepository {

    private final ObjetivoPersistenceMapper mapper;
    private final ObjetivoJpaRepository jpaRepository;

    @Override
    public Objetivo save(Objetivo objetivo) {
        var entity = mapper.toEntity(objetivo);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Objetivo> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Objetivo> findByUsuario(UUID fkUsuario) {
        return jpaRepository.findByFkUsuario(fkUsuario).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Objetivo> findByNomeAndUsuario(String nome, UUID fkUsuario) {
        return jpaRepository.findByFkUsuarioAndNomeContainingIgnoreCase(fkUsuario, nome).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

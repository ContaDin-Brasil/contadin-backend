package br.com.contadin.infrastructure.persistence.repository.instituicao;

import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.domain.model.Instituicao;
import br.com.contadin.infrastructure.persistence.entity.InstituicaoEntity;
import br.com.contadin.infrastructure.persistence.mapper.InstituicaoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InstituicaoRepositoryImplementations implements InstituicaoRepository {

    private final InstituicaoPersistenceMapper mapper;
    private final InstituicaoJpaRepository jpaRepository;

    @Override
    public Instituicao save(Instituicao instituicao){
        InstituicaoEntity entity = mapper.toEntity(instituicao);
        jpaRepository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<Instituicao> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Instituicao> findAtivasByUsuario(UUID fkUsuario) {
        return jpaRepository
                .findByFkUsuarioAndAtivoTrue(fkUsuario)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Instituicao> findByNomeAndUsuario(UUID fkUsuario, String nome){
        return jpaRepository
                .findByFkUsuarioAndNome(fkUsuario, nome)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

}

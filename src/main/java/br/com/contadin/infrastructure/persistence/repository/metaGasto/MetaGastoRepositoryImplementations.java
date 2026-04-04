package br.com.contadin.infrastructure.persistence.repository.metaGasto;

import br.com.contadin.application.port.out.MetaGastoRepository;
import br.com.contadin.domain.model.MetaGasto;
import br.com.contadin.infrastructure.persistence.entity.MetaGastoEntity;
import br.com.contadin.infrastructure.persistence.mapper.MetaGastoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MetaGastoRepositoryImplementations implements MetaGastoRepository {
        private final MetaGastoPersistenceMapper mapper;
        private final MetaGastoJpaRepository jpaRepository;

        @Override
        public MetaGasto save(MetaGasto meta){
            MetaGastoEntity entity = mapper.toEntity(meta);
            jpaRepository.save(entity);
            return mapper.toDomain(entity);
        }

         @Override
        public Optional<MetaGasto> findById(UUID id) {
            return jpaRepository.findById(id)
                    .map(mapper::toDomain);
        }

        @Override
        public void deleteById(UUID id) {
            jpaRepository.deleteById(id);
        }

        @Override
        public java.util.List<MetaGasto> findByUsuario(Integer fkUsuario) {
            return jpaRepository.findByFkUsuario(fkUsuario)
                    .stream()
                    .map(mapper::toDomain)
                    .toList();
        }

        @Override
        public java.util.List<MetaGasto> findByNomeAndUsuario(String nome, Integer fkUsuario) {
            return jpaRepository.findByFkUsuarioAndNome(fkUsuario, nome)
                    .stream().map(mapper::toDomain)
                    .toList();
        }
}

package br.com.contadin.infrastructure.persistence.mapper;

import br.com.contadin.domain.model.Instituicao;
import br.com.contadin.infrastructure.persistence.entity.InstituicaoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InstituicaoPersistenceMapper {

        @Mapping(target = "fkUsuario", source = "fkUsuario")
        InstituicaoEntity toEntity(Instituicao instituicao);

        @Mapping(target = "fkUsuario", source = "fkUsuario")
        Instituicao toDomain(InstituicaoEntity entity);
}

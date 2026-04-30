package br.com.contadin.infrastructure.web.mapper;

import br.com.contadin.application.dto.instituicao.InstituicaoRequest;
import br.com.contadin.application.dto.instituicao.InstituicaoResponse;
import br.com.contadin.domain.model.Instituicao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InstituicaoWebMapper {

        @Mapping(target = "fkUsuario", source = "fkUsuario")
        Instituicao toDomain(InstituicaoRequest request);

        InstituicaoResponse toResponse(Instituicao request);
}

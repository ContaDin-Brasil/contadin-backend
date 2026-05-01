package br.com.contadin.infrastructure.web.mapper;

import br.com.contadin.application.dto.objetivo.ObjetivoRequest;
import br.com.contadin.application.dto.objetivo.ObjetivoResponse;
import br.com.contadin.domain.model.Objetivo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ObjetivoWebMapper {

    @Mapping(target = "fkUsuario", source = "fkUsuario")
    @Mapping(target = "fkCategoria", source = "fkCategoria")
    Objetivo toDomain(ObjetivoRequest request);

    ObjetivoResponse toResponse(Objetivo request);
}

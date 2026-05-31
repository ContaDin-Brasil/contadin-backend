package br.com.contadin.infrastructure.web.mapper;

import br.com.contadin.application.dto.objetivo.ObjetivoRequest;
import br.com.contadin.application.dto.objetivo.ObjetivoResponse;
import br.com.contadin.domain.model.Objetivo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ObjetivoWebMapper {

    Objetivo toDomain(ObjetivoRequest request);

    ObjetivoResponse toResponse(Objetivo objetivo);
}

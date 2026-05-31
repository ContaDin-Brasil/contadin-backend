package br.com.contadin.infrastructure.web.mapper;

import br.com.contadin.application.dto.saldo.SaldoDiarioResponse;
import br.com.contadin.domain.model.SaldoDiario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaldoDiarioWebMapper {
    SaldoDiarioResponse toResponse(SaldoDiario saldoDiario);
}

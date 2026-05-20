package br.com.contadin.infrastructure.persistence.mapper;

import br.com.contadin.domain.model.SaldoDiario;
import br.com.contadin.infrastructure.persistence.entity.SaldoDiarioEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaldoDiarioPersistenceMapper {
    SaldoDiarioEntity toEntity(SaldoDiario saldoDiario);
    SaldoDiario toDomain(SaldoDiarioEntity entity);
}

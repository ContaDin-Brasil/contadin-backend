package br.com.contadin.infrastructure.persistence.mapper;

import br.com.contadin.domain.model.Transacao;
import br.com.contadin.infrastructure.persistence.entity.TransacaoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransacaoPersistenceMapper {
    TransacaoEntity toEntity(Transacao transacao);
    Transacao toDomain (TransacaoEntity entity);
}

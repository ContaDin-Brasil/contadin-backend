package br.com.contadin.infrastructure.persistence.mapper;

import br.com.contadin.domain.model.TokenRecuperarSenha;
import br.com.contadin.infrastructure.persistence.entity.TokenRecuperarSenhaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenRecuperarSenhaPersistenceMapper {

    TokenRecuperarSenhaEntity toEntity(TokenRecuperarSenha tokenRecuperarSenha);

    TokenRecuperarSenha toDomain(TokenRecuperarSenhaEntity tokenRecuperarSenhaEntity);

}

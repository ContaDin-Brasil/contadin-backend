package br.com.contadin.application.mapper.auth;

import br.com.contadin.application.dto.usuario.auth.LoginOutputDTO;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.domain.valueobject.Email;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface LoginMapper {

    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "id", source = "usuario.id")
    @Mapping(target = "nome", source = "usuario.nome")
    @Mapping(target = "sobrenome", source = "usuario.sobrenome")
    @Mapping(target = "email", source = "usuario.email", qualifiedByName = "emailToString")
    @Mapping(target = "ativo", source = "usuario.ativo")
    LoginOutputDTO toOutput(Usuario usuario, String accessToken);

    @Named("emailToString")
    default String emailToString(Email email) {
        return Objects.requireNonNull(email, "Email do usuário não pode ser nulo no login").valor();
    }
}

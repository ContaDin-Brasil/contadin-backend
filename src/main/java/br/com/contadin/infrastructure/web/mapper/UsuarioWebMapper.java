package br.com.contadin.infrastructure.web.mapper;

import br.com.contadin.application.dto.usuario.UsuarioPostRequest;
import br.com.contadin.application.dto.usuario.UsuarioPostResponse;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.domain.valueobject.Email;
import br.com.contadin.domain.valueobject.Telefone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UsuarioWebMapper {

    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmail")
    @Mapping(target = "telefone", source = "telefone", qualifiedByName = "stringToTelefone")
    Usuario toDomain(UsuarioPostRequest request);

    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    @Mapping(target = "telefone", source = "telefone", qualifiedByName = "telefoneToString")
    UsuarioPostResponse toResponse(Usuario model);

    // ======================
    // Email
    // ======================

    @Named("stringToEmail")
    default Email stringToEmail(String email) {
        return email == null ? null : new Email(email);
    }

    @Named("emailToString")
    default String emailToString(Email email) {
        return email == null ? null : email.valor();
    }

    // ======================
    // Telefone
    // ======================

    @Named("stringToTelefone")
    default Telefone stringToTelefone(String telefone) {
        return telefone == null ? null : new Telefone(telefone);
    }

    @Named("telefoneToString")
    default String telefoneToString(Telefone telefone) {
        return telefone == null ? null : telefone.numero();
    }
}

package br.com.contadin.infrastructure.web.mapper;

import br.com.contadin.application.dto.usuario.UsuarioPatchRequest;
import br.com.contadin.application.dto.usuario.UsuarioPostRequest;
import br.com.contadin.application.dto.usuario.UsuarioPostResponse;
import br.com.contadin.application.dto.usuario.UsuarioResponse;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.domain.valueobject.Email;
import br.com.contadin.domain.valueobject.Telefone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface UsuarioWebMapper {

    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmail")
    @Mapping(target = "telefone", source = "telefone", qualifiedByName = "stringToTelefone")
    Usuario toDomain(UsuarioPostRequest request);

    @Mapping(target = "telefone", source = "telefone", qualifiedByName = "stringToTelefone")
    Usuario toDomain(UsuarioPatchRequest request);

    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    @Mapping(target = "telefone", source = "telefone", qualifiedByName = "telefoneToString")
    UsuarioPostResponse toResponse(Usuario model);

    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    @Mapping(target = "telefone", source = "telefone", qualifiedByName = "telefoneToString")
    UsuarioResponse toUsuarioResponse(Usuario model);

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

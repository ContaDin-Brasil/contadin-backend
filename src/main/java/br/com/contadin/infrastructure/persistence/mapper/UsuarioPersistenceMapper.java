package br.com.contadin.infrastructure.persistence.mapper;

import br.com.contadin.domain.model.Usuario;
import br.com.contadin.domain.valueobject.Email;
import br.com.contadin.domain.valueobject.Telefone;
import br.com.contadin.infrastructure.persistence.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UsuarioPersistenceMapper {

    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    @Mapping(target = "telefone", source = "telefone", qualifiedByName = "telefoneToString")
    UsuarioEntity toEntity(Usuario usuario);

    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmail")
    @Mapping(target = "telefone", source = "telefone", qualifiedByName = "stringToTelefone")
    Usuario toDomain(UsuarioEntity entity);

    // ======================
    // Email
    // ======================

    @Named("emailToString")
    default String emailToString(Email email) {
        return email == null ? null : email.valor();
    }

    @Named("stringToEmail")
    default Email stringToEmail(String email) {
        return email == null ? null : new Email(email);
    }

    // ======================
    // Telefone
    // ======================

    @Named("telefoneToString")
    default String telefoneToString(Telefone telefone) {
        return telefone == null ? null : telefone.numero();
    }

    @Named("stringToTelefone")
    default Telefone stringToTelefone(String telefone) {
        return telefone == null ? null : new Telefone(telefone);
    }
}

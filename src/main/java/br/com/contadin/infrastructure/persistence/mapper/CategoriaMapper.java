package br.com.contadin.infrastructure.persistence.mapper;

import br.com.contadin.domain.model.Categoria;
import br.com.contadin.infrastructure.persistence.entity.CategoriaEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public Categoria toDomain(CategoriaEntity entity) {
        return new Categoria(
                entity.getId(),
                entity.getNome()
                // entity.getFkUsuario()

        );
    }
}

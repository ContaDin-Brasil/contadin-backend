package br.com.contadin.infrastructure.persistence.mapper;

import br.com.contadin.domain.model.Categoria;
import br.com.contadin.infrastructure.persistence.entity.CategoriaEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public CategoriaEntity toEntity(Categoria categoria) {

        return CategoriaEntity.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .fkUsuario(categoria.getFkUsuario())
                .build();
    }

    public Categoria toDomain(CategoriaEntity entity) {

        return Categoria.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .fkUsuario(entity.getFkUsuario())
                .build();
    }
}

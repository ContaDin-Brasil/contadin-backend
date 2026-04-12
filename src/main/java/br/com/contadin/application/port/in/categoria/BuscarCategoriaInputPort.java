package br.com.contadin.application.port.in.categoria;

import br.com.contadin.domain.enums.TipoCategoria;
import br.com.contadin.domain.model.Categoria;

import java.util.List;
import java.util.UUID;

public interface BuscarCategoriaInputPort {
    List<Categoria> execute(UUID fkUsuario, TipoCategoria tipoCategoria);

    List<Categoria> executeBuscarInativas(UUID fkUsuario);

    Categoria executeBuscarPorId(UUID categoriaId);

    List<Categoria> executeBuscarPorNome(String nome, UUID fkUsuario, TipoCategoria tipoCategoria);
}
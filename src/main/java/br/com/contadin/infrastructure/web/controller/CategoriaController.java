package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.categoria.CategoriaRequest;
import br.com.contadin.application.dto.categoria.CategoriaResponse;
import br.com.contadin.application.port.in.categoria.CriarCategoriaInputPort;
import br.com.contadin.domain.model.Categoria;
import br.com.contadin.infrastructure.web.mapper.CategoriaWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categorias")
public class CategoriaController {

    private final CriarCategoriaInputPort criarCategoriaInputPort;
    private final CategoriaWebMapper categoriaWebMapper;

    @PostMapping
    public ResponseEntity<CategoriaResponse> criar(
            @RequestBody CategoriaRequest request
    ) {
        Categoria categoria = categoriaWebMapper.toDomain(request);

        Categoria categoriaCriada = criarCategoriaInputPort.execute(categoria);

        CategoriaResponse response = categoriaWebMapper.toResponse(categoriaCriada);

        return ResponseEntity.status(201).body(response);
    }
}
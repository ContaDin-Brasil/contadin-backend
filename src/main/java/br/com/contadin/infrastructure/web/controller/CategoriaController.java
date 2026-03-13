package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.categoria.CategoriaRequest;
import br.com.contadin.application.dto.categoria.CategoriaResponse;
import br.com.contadin.application.port.in.categoria.CriarCategoriaInputPort;
import br.com.contadin.domain.model.Categoria;
import br.com.contadin.infrastructure.web.mapper.CategoriaWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categorias")
@Tag(name = "Categorias", description = "Gerenciamento de categorias")
public class CategoriaController {

    private final CriarCategoriaInputPort criarCategoriaInputPort;
    private final CategoriaWebMapper categoriaWebMapper;

    @PostMapping
    @Operation(summary = "Criar uma nova categoria", description = "Cria uma nova categoria no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoriaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content)
    })
    public ResponseEntity<CategoriaResponse> criar(
            @Valid @RequestBody CategoriaRequest request
    ) {
        Categoria categoria = categoriaWebMapper.toDomain(request);

        Categoria categoriaCriada = criarCategoriaInputPort.execute(categoria);

        CategoriaResponse response = categoriaWebMapper.toResponse(categoriaCriada);

        return ResponseEntity.status(201).body(response);
    }
}
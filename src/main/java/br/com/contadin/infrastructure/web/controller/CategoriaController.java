package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.categoria.CategoriaRequest;
import br.com.contadin.application.dto.categoria.CategoriaResponse;
import br.com.contadin.application.dto.categoria.GastoPorCategoriaResponse;
import br.com.contadin.application.port.in.categoria.AtualizarCategoriaInputPort;
import br.com.contadin.application.port.in.categoria.AlternarStatusCategoriaInputPort;
import br.com.contadin.application.port.in.categoria.BuscarCategoriaInputPort;
import br.com.contadin.application.port.in.categoria.BuscarGastoPorCategoriaInputPort;
import br.com.contadin.application.port.in.categoria.CriarCategoriaInputPort;
import br.com.contadin.application.port.in.categoria.DeletarCategoriaInputPort;
import br.com.contadin.domain.enums.TipoCategoria;
import br.com.contadin.domain.model.Categoria;
import br.com.contadin.infrastructure.web.mapper.CategoriaWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categorias")
@Tag(name = "Categorias", description = "Gerenciamento de categorias")
public class CategoriaController {

    private final CriarCategoriaInputPort criarCategoriaInputPort;
    private final AtualizarCategoriaInputPort atualizarCategoriaInputPort;
    private final BuscarCategoriaInputPort buscarCategoriaInputPort;
    private final BuscarGastoPorCategoriaInputPort buscarGastoPorCategoriaInputPort;
    private final DeletarCategoriaInputPort deletarCategoriaInputPort;
    private final AlternarStatusCategoriaInputPort alternarStatusCategoriaInputPort;
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

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar categoria", description = "Atualiza parcialmente uma categoria existente.")
    public ResponseEntity<CategoriaResponse> atualizar(
            @PathVariable UUID id,
            @RequestBody CategoriaRequest request
    ) {
        Categoria categoria = categoriaWebMapper.toDomain(request);
        Categoria atualizada = atualizarCategoriaInputPort.execute(id, categoria);
        CategoriaResponse response = categoriaWebMapper.toResponse(atualizada);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID", description = "Retorna uma categoria específica pelo ID.")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable UUID id) {
        Categoria categoria = buscarCategoriaInputPort.executeBuscarPorId(id);
        CategoriaResponse response = categoriaWebMapper.toResponse(categoria);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @Operation(summary = "Listar todas as categorias por usuário", description = "Retorna todas as categorias ativas do usuário informado, independente do tipo.")
    public ResponseEntity<List<CategoriaResponse>> listarTodasPorUsuario(
            @RequestParam UUID fkUsuario
    ) {
        List<CategoriaResponse> response = buscarCategoriaInputPort.executeBuscarTodas(fkUsuario)
                .stream()
                .map(categoriaWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar categorias por usuário", description = "Retorna todas as categorias do usuário informado.")
    public ResponseEntity<List<CategoriaResponse>> listarPorUsuario(
            @RequestParam UUID fkUsuario,
            @RequestParam TipoCategoria tipoCategoria
    ) {
        List<CategoriaResponse> response = buscarCategoriaInputPort.execute(fkUsuario, tipoCategoria)
                .stream()
                .map(categoriaWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/inativas")
    @Operation(summary = "Listar categorias inativas por usuário", description = "Retorna todas as categorias inativas do usuário informado.")
    public ResponseEntity<List<CategoriaResponse>> listarCategoriasInativas(@RequestParam UUID fkUsuario) {
        List<CategoriaResponse> response = buscarCategoriaInputPort.executeBuscarInativas(fkUsuario)
                .stream()
                .map(categoriaWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nome")
    @Operation(summary = "Buscar categorias por nome", description = "Retorna categorias do usuário filtrando por nome.")
    public ResponseEntity<List<CategoriaResponse>> buscarPorNome(
            @RequestParam String nome,
            @RequestParam UUID fkUsuario,
            @RequestParam TipoCategoria tipoCategoria
    ) {
        List<CategoriaResponse> response = buscarCategoriaInputPort.executeBuscarPorNome(nome, fkUsuario, tipoCategoria)
                .stream()
                .map(categoriaWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir categoria", description = "Realiza exclusão física de uma categoria.")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        deletarCategoriaInputPort.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/alternar-status")
    @Operation(summary = "Alternar status da categoria", description = "Alterna o status lógico da categoria entre ativa e inativa.")
    public ResponseEntity<Void> alternarStatus(@PathVariable UUID id) {
        alternarStatusCategoriaInputPort.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/gastos")
    @Operation(
            summary = "Gasto por categoria no período",
            description = """
                    Retorna o total gasto em cada categoria de GASTO no período informado,
                    ordenado do maior para o menor valor. Inclui nome, ícone e cor da categoria
                    para exibição direta no frontend, além do percentual de cada categoria
                    sobre o total geral do período.
                    Se `fkInstituicao` for informado, considera apenas aquela instituição;
                    caso contrário, consolida todas as instituições ativas do usuário.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Gastos por categoria retornados com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = GastoPorCategoriaResponse.class),
                                    examples = @ExampleObject(value = """
                                            [
                                              {
                                                "fkCategoria": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                                "nome": "Alimentação",
                                                "icone": "restaurant",
                                                "cor": "#FF6B6B",
                                                "total": 850.00,
                                                "percentual": 56.67
                                              },
                                              {
                                                "fkCategoria": "7cb91a23-1234-4321-a1b2-9f8e7d6c5b4a",
                                                "nome": "Transporte",
                                                "icone": "directions-car",
                                                "cor": "#4ECDC4",
                                                "total": 320.00,
                                                "percentual": 43.33
                                              }
                                            ]
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content)
            }
    )
    public ResponseEntity<List<GastoPorCategoriaResponse>> buscarGastoPorCategoria(
            @Parameter(description = "UUID do usuário", required = true)
            @RequestParam UUID fkUsuario,
            @Parameter(description = "Mês (1-12)", required = true, example = "5")
            @RequestParam int mes,
            @Parameter(description = "Ano", required = true, example = "2025")
            @RequestParam int ano,
            @Parameter(description = "UUID da instituição (opcional). Se omitido, consolida todas as instituições.")
            @RequestParam(required = false) UUID fkInstituicao
    ) {
        return ResponseEntity.ok(
                buscarGastoPorCategoriaInputPort.execute(fkUsuario, mes, ano, fkInstituicao)
        );
    }
}
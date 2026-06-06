package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.transacao.TransacaoConsultaParams;
import br.com.contadin.application.dto.transacao.TransacaoPaginadaResponse;
import br.com.contadin.application.dto.transacao.TransacaoRequest;
import br.com.contadin.application.dto.transacao.TransacaoResponse;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.application.port.in.transacao.CriarMultiplasTransacoesInputPort;
import br.com.contadin.application.port.in.transacao.AtualizarTransacaoInputPort;
import br.com.contadin.application.port.in.transacao.BuscarTransacaoInputPort;
import br.com.contadin.application.port.in.transacao.CriarTransacaoInputPort;
import br.com.contadin.application.port.in.transacao.DeletarTransacaoInputPort;
import br.com.contadin.application.port.in.transacao.DeletarMultiplasTransacoesInputPort;
import br.com.contadin.application.port.in.transacao.DesativarTransacaoInputPort;
import br.com.contadin.application.port.in.transacao.DesativarMultiplasTransacoesInputPort;
import br.com.contadin.infrastructure.web.mapper.TransacaoWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/transacao"})
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Gerenciamento de transações")
public class TransacaoController {

    private final CriarTransacaoInputPort criarTransacaoInputPort;
    private final CriarMultiplasTransacoesInputPort criarMultiplasTransacoesInputPort;
    private final BuscarTransacaoInputPort buscarTransacaoInputPort;
    private final AtualizarTransacaoInputPort atualizarTransacaoInputPort;
    private final DeletarTransacaoInputPort deletarTransacaoInputPort;
    private final DeletarMultiplasTransacoesInputPort deletarMultiplasTransacoesInputPort;
    private final DesativarTransacaoInputPort desativarTransacaoInputPort;
    private final DesativarMultiplasTransacoesInputPort desativarMultiplasTransacoesInputPort;
    private final TransacaoWebMapper transacaoWebMapper;

    @PostMapping("/lote")
    @Operation(
        summary = "Criar múltiplas transações",
        description = "Cria uma lista de transações em uma única operação. Se qualquer transação for inválida, nenhuma é salva.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TransacaoRequest.class),
                examples = @ExampleObject(value = """
                    [
                      {
                        "valor": 100.50,
                        "tipo": "GASTO",
                        "descricao": "Mercado",
                        "dataTransacao": "2026-04-21T20:00:00",
                        "parcelado": false,
                        "qtdParcelas": null,
                        "recorrencia": "MENSAL",
                        "fimRecorrencia": "2026-12-31",
                        "ativo": true,
                        "fkInstituicao": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                        "fkCategoria": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                      },
                      {
                        "valor": 250.00,
                        "tipo": "RECEITA",
                        "descricao": "Salário",
                        "dataTransacao": "2026-04-21T08:00:00",
                        "parcelado": false,
                        "qtdParcelas": null,
                        "recorrencia": null,
                        "fimRecorrencia": null,
                        "ativo": true,
                        "fkInstituicao": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                        "fkCategoria": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                      }
                    ]
                    """)
            )
        )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transações criadas com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransacaoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida — indica qual transação falhou",
                    content = @Content)
    })
    public ResponseEntity<List<TransacaoResponse>> criarMultiplasTransacoes(@Valid @RequestBody List<TransacaoRequest> requests) {
        var transacoes = requests.stream().map(transacaoWebMapper::toDomain).toList();
        var criadas = criarMultiplasTransacoesInputPort.execute(transacoes);
        var response = criadas.stream().map(transacaoWebMapper::toResponse).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping
    @Operation(
        summary = "Criar uma nova transação",
        description = "Cria uma nova transação no sistema.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TransacaoRequest.class),
                examples = @ExampleObject(value = """
                    {
                      "valor": 100.50,
                      "tipo": "GASTO",
                      "descricao": "Mercado",
                      "dataTransacao": "2026-04-21T20:00:00",
                      "parcelado": false,
                      "qtdParcelas": null,
                      "recorrencia": "MENSAL",
                      "fimRecorrencia": "2026-12-31",
                      "ativo": true,
                      "fkInstituicao": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                      "fkCategoria": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
                    }
                    """)
            )
        )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transação criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransacaoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content)
    })
    public ResponseEntity<TransacaoResponse> criarTransacao(@Valid @RequestBody TransacaoRequest request) {
        var transacao = transacaoWebMapper.toDomain(request);
        var transacaoCriada = criarTransacaoInputPort.execute(transacao);
        var response = transacaoWebMapper.toResponse(transacaoCriada);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

        @GetMapping
        @Operation(summary = "Listar transações com filtros", description = "Retorna transações paginadas com filtros dinâmicos e ordenação.")
        public ResponseEntity<TransacaoPaginadaResponse> listarTransacoes(
            @RequestParam(name = "_page", required = false) Integer page,
            @RequestParam(name = "_limit", required = false) Integer limit,
            @RequestParam(name = "_sort", required = false) String sort,
            @RequestParam(name = "_order", required = false) String order,
            @RequestParam(name = "tipo", required = false) TipoTransacao tipo,
            @RequestParam(name = "fk_instituicao", required = false) UUID fkInstituicao,
            @RequestParam(name = "fk_categoria", required = false) UUID fkCategoria,
            @Parameter(description = "Valor minimo do filtro (maior ou igual). Ex.: valor_gte=100 retorna transacoes com valor >= 100")
            @RequestParam(name = "valor_gte", required = false) Double valorGte,
            @Parameter(description = "Valor maximo do filtro (menor ou igual). Ex.: valor_lte=500 retorna transacoes com valor <= 500")
            @RequestParam(name = "valor_lte", required = false) Double valorLte,
            @RequestParam(name = "parcelado", required = false) Boolean parcelado,
            @RequestParam(name = "recorrente", required = false) Boolean recorrente,
            @Parameter(description = "Data/hora inicial do filtro (maior ou igual). Aceita yyyy-MM-dd ou yyyy-MM-dd'T'HH:mm:ss. Ex.: data_transacao_gte=2026-03-01")
            @RequestParam(name = "data_transacao_gte", required = false) String dataTransacaoGte,
            @Parameter(description = "Data/hora final do filtro (menor ou igual). Aceita yyyy-MM-dd ou yyyy-MM-dd'T'HH:mm:ss. Ex.: data_transacao_lte=2026-03-31")
            @RequestParam(name = "data_transacao_lte", required = false) String dataTransacaoLte,
            @RequestParam(name = "search", required = false) String search
        ) {
        TransacaoConsultaParams params = new TransacaoConsultaParams(
            page,
            limit,
            sort,
            order,
            tipo,
            fkInstituicao,
            fkCategoria,
            valorGte,
            valorLte,
            parcelado,
            recorrente,
            dataTransacaoGte,
            dataTransacaoLte,
            search
        );

        var transacoesPage = buscarTransacaoInputPort.execute(params);

        List<TransacaoResponse> data = transacoesPage.getContent().stream()
            .map(transacaoWebMapper::toResponse)
            .toList();

        TransacaoPaginadaResponse response = new TransacaoPaginadaResponse(
            data,
            transacoesPage.getNumber() + 1,
            transacoesPage.getSize(),
            transacoesPage.getTotalElements(),
            transacoesPage.getTotalPages(),
            transacoesPage.hasNext()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar transação por ID", description = "Retorna uma transação específica pelo ID.")
    public ResponseEntity<TransacaoResponse> buscarTransacaoPorId(@PathVariable UUID id) {
        var transacao = buscarTransacaoInputPort.executeBuscarPorId(id);
        var response = transacaoWebMapper.toResponse(transacao);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar transação parcialmente", description = "Atualiza parcialmente os dados de uma transação existente.")
    public ResponseEntity<TransacaoResponse> atualizarTransacao(
            @PathVariable UUID id,
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para atualizar parcialmente uma transação",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TransacaoRequest.class))
            )
            TransacaoRequest request) {
        var transacao = transacaoWebMapper.toDomain(request);
        var transacaoAtualizada = atualizarTransacaoInputPort.execute(id, transacao);
        var response = transacaoWebMapper.toResponse(transacaoAtualizada);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar transação", description = "Remove fisicamente uma transação.")
    public ResponseEntity<Void> deletarTransacao(@PathVariable UUID id) {
        deletarTransacaoInputPort.execute(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/lote")
    @Operation(summary = "Deletar transações em lote", description = "Remove fisicamente uma lista de transações. Se qualquer ID não existir, nenhuma é deletada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transações deletadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Lista de IDs vazia", content = @Content),
            @ApiResponse(responseCode = "404", description = "Uma ou mais transações não encontradas", content = @Content)
    })
    public ResponseEntity<Void> deletarMultiplasTransacoes(@RequestBody List<UUID> ids) {
        deletarMultiplasTransacoesInputPort.execute(ids);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar transação", description = "Realiza exclusão lógica da transação, marcando-a como inativa.")
    public ResponseEntity<Void> desativarTransacao(@PathVariable UUID id) {
        desativarTransacaoInputPort.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/lote/desativar")
    @Operation(summary = "Desativar transações em lote", description = "Desativa logicamente uma lista de transações (ativo=false). Se qualquer ID não existir, nenhuma é alterada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transações desativadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Lista de IDs vazia", content = @Content),
            @ApiResponse(responseCode = "404", description = "Uma ou mais transações não encontradas", content = @Content)
    })
    public ResponseEntity<Void> desativarMultiplasTransacoes(@RequestBody List<UUID> ids) {
        desativarMultiplasTransacoesInputPort.execute(ids);
        return ResponseEntity.noContent().build();
    }
}
package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.transacao.TransacaoRequest;
import br.com.contadin.application.dto.transacao.TransacaoResponse;
import br.com.contadin.application.port.in.transacao.AtualizarTransacaoInputPort;
import br.com.contadin.application.port.in.transacao.BuscarTransacaoInputPort;
import br.com.contadin.application.port.in.transacao.CriarTransacaoInputPort;
import br.com.contadin.application.port.in.transacao.DeletarTransacaoInputPort;
import br.com.contadin.application.port.in.transacao.DesativarTransacaoInputPort;
import br.com.contadin.infrastructure.web.mapper.TransacaoWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Gerenciamento de transações")
public class TransacaoController {

    private final CriarTransacaoInputPort criarTransacaoInputPort;
    private final BuscarTransacaoInputPort buscarTransacaoInputPort;
    private final AtualizarTransacaoInputPort atualizarTransacaoInputPort;
    private final DeletarTransacaoInputPort deletarTransacaoInputPort;
    private final DesativarTransacaoInputPort desativarTransacaoInputPort;
    private final TransacaoWebMapper transacaoWebMapper;

    @PostMapping
    @Operation(summary = "Criar uma nova transação", description = "Cria uma nova transação no sistema.")
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
    @Operation(summary = "Listar todas as transações", description = "Retorna todas as transações cadastradas.")
    public ResponseEntity<List<TransacaoResponse>> listarTransacoes() {
        List<TransacaoResponse> response = buscarTransacaoInputPort.execute().stream()
                .map(transacaoWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar transação por ID", description = "Retorna uma transação específica pelo ID.")
    public ResponseEntity<TransacaoResponse> buscarTransacaoPorId(@PathVariable Integer id) {
        var transacao = buscarTransacaoInputPort.executeBuscarPorId(id);
        var response = transacaoWebMapper.toResponse(transacao);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar transação parcialmente", description = "Atualiza parcialmente os dados de uma transação existente.")
    public ResponseEntity<TransacaoResponse> atualizarTransacao(
            @PathVariable Integer id,
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
    public ResponseEntity<Void> deletarTransacao(@PathVariable Integer id) {
        deletarTransacaoInputPort.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar transação", description = "Realiza exclusão lógica da transação, marcando-a como inativa.")
    public ResponseEntity<Void> desativarTransacao(@PathVariable Integer id) {
        desativarTransacaoInputPort.execute(id);
        return ResponseEntity.noContent().build();
    }
}
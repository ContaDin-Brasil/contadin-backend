package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.transacao.TransacaoRequest;
import br.com.contadin.application.dto.transacao.TransacaoResponse;
import br.com.contadin.application.port.in.transacao.CriarTransacaoInputPort;
import br.com.contadin.infrastructure.web.mapper.TransacaoWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Gerenciamento de transações")
public class TransacaoController {

    private final CriarTransacaoInputPort criarTransacaoInputPort;
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
    public ResponseEntity<TransacaoResponse> criarTransacao(@RequestBody TransacaoRequest request) {
        var transacao = transacaoWebMapper.toDomain(request);
        var transacaoCriada = criarTransacaoInputPort.execute(transacao);
        var response = transacaoWebMapper.toResponse(transacaoCriada);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.transacao.TransacaoRequest;
import br.com.contadin.application.dto.transacao.TransacaoResponse;
import br.com.contadin.application.port.in.transacao.CriarTransacaoInputPort;
import br.com.contadin.infrastructure.web.mapper.TransacaoWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

    private final CriarTransacaoInputPort criarTransacaoInputPort;
    private final TransacaoWebMapper transacaoWebMapper;

    @PostMapping
    public ResponseEntity<TransacaoResponse> criarTransacao(@RequestBody TransacaoRequest request) {
        var transacao = transacaoWebMapper.toDomain(request);
        var transacaoCriada = criarTransacaoInputPort.execute(transacao);
        var response = transacaoWebMapper.toResponse(transacaoCriada);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
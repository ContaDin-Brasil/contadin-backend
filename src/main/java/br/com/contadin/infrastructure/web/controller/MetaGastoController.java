package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.metaGasto.MetaGastoRequest;
import br.com.contadin.application.dto.metaGasto.MetaGastoResponse;
import br.com.contadin.application.port.in.metaGasto.AtualizarMetaGastoInputPort;
import br.com.contadin.application.port.in.metaGasto.BuscarMetaGastoInputPort;
import br.com.contadin.application.port.in.metaGasto.CriarMetaGastoInputPort;
import br.com.contadin.application.port.in.metaGasto.DeletarMetaGastoInputPort;
import br.com.contadin.domain.model.MetaGasto;
import br.com.contadin.infrastructure.web.mapper.MetaGastoWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meta-gastos")
public class MetaGastoController {

    private final CriarMetaGastoInputPort criarMetaGastoInputPort;
    private final AtualizarMetaGastoInputPort atualizarMetaGastoInputPort;
    private final BuscarMetaGastoInputPort buscarMetaGastoInputPort;
    private final DeletarMetaGastoInputPort deletarMetaGastoInputPort;

    private final MetaGastoWebMapper metaGastoWebMapper;

    @PostMapping
    public ResponseEntity<MetaGastoResponse> criarMetaGasto(
            @Valid @RequestBody MetaGastoRequest request
    ) {
        MetaGasto metaGasto = metaGastoWebMapper.toDomain(request);
        MetaGasto metaGastoCriada = criarMetaGastoInputPort.execute(metaGasto);
        MetaGastoResponse response = metaGastoWebMapper.toResponse(metaGastoCriada);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

     @PatchMapping("/{id}")
    public ResponseEntity<MetaGastoResponse> atualizarMetaGasto( @PathVariable Integer id, @Valid @RequestBody MetaGastoRequest request) {
         MetaGasto metaGasto = metaGastoWebMapper.toDomain(request);
         MetaGasto metaGastoCriada = atualizarMetaGastoInputPort.execute(id, metaGasto);
         MetaGastoResponse response = metaGastoWebMapper.toResponse(metaGastoCriada);
         return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetaGastoResponse> buscarMetaGastoPorId(@PathVariable Integer id) {
        MetaGasto metaGasto = buscarMetaGastoInputPort.executeBuscarPorId(id);
        MetaGastoResponse response = metaGastoWebMapper.toResponse(metaGasto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MetaGastoResponse>> buscarMetaGastoPorUsuario(@RequestParam Integer fkUsuario) {
        java.util.List<MetaGasto> metaGastos = buscarMetaGastoInputPort.execute(fkUsuario);
        java.util.List<MetaGastoResponse> response = metaGastos.stream()
                .map(metaGastoWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nome")
    public ResponseEntity<List<MetaGastoResponse>> buscarMetaGastoPorNome(@RequestParam String nome, @RequestParam Integer fkUsuario) {
        java.util.List<MetaGasto> metaGastos = buscarMetaGastoInputPort.executeBuscarPorNome(nome, fkUsuario);
        java.util.List<MetaGastoResponse> response = metaGastos.stream()
                .map(metaGastoWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMetaGasto(@PathVariable Integer id) {
        deletarMetaGastoInputPort.execute(id);
        return ResponseEntity.noContent().build();
    }

}

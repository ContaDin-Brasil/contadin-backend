package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.objetivo.ObjetivoRequest;
import br.com.contadin.application.dto.objetivo.ObjetivoResponse;
import br.com.contadin.application.port.in.objetivo.AtualizarObjetivoInputPort;
import br.com.contadin.application.port.in.objetivo.BuscarObjetivoInputPort;
import br.com.contadin.application.port.in.objetivo.CriarObjetivoInputPort;
import br.com.contadin.application.port.in.objetivo.DeletarObjetivoInputPort;
import br.com.contadin.domain.model.Objetivo;
import br.com.contadin.infrastructure.web.mapper.ObjetivoWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/objetivos")
public class ObjetivoController {

    private final CriarObjetivoInputPort criarObjetivoInputPort;
    private final AtualizarObjetivoInputPort atualizarObjetivoInputPort;
    private final BuscarObjetivoInputPort buscarObjetivoInputPort;
    private final DeletarObjetivoInputPort deletarObjetivoInputPort;

    private final ObjetivoWebMapper objetivoWebMapper;

    @PostMapping
    public ResponseEntity<ObjetivoResponse> criarObjetivo(
            @Valid @RequestBody ObjetivoRequest request
    ) {
        Objetivo objetivo = objetivoWebMapper.toDomain(request);
        Objetivo objetivoCriada = criarObjetivoInputPort.execute(objetivo);
        ObjetivoResponse response = objetivoWebMapper.toResponse(objetivoCriada);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

     @PatchMapping("/{id}")
    public ResponseEntity<ObjetivoResponse> atualizarObjetivo(@PathVariable UUID id, @Valid @RequestBody ObjetivoRequest request) {
         Objetivo objetivo = objetivoWebMapper.toDomain(request);
         Objetivo objetivoCriada = atualizarObjetivoInputPort.execute(id, objetivo);
         ObjetivoResponse response = objetivoWebMapper.toResponse(objetivoCriada);
         return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjetivoResponse> buscarObjetivoPorId(@PathVariable UUID id) {
        Objetivo objetivo = buscarObjetivoInputPort.executeBuscarPorId(id);
        ObjetivoResponse response = objetivoWebMapper.toResponse(objetivo);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ObjetivoResponse>> buscarObjetivoPorUsuario(@RequestParam UUID fkUsuario) {
        java.util.List<Objetivo> objetivos = buscarObjetivoInputPort.execute(fkUsuario);
        java.util.List<ObjetivoResponse> response = objetivos.stream()
                .map(objetivoWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nome")
    public ResponseEntity<List<ObjetivoResponse>> buscarObjetivoPorNome(@RequestParam String nome, @RequestParam UUID fkUsuario) {
        java.util.List<Objetivo> objetivos = buscarObjetivoInputPort.executeBuscarPorNome(nome, fkUsuario);
        java.util.List<ObjetivoResponse> response = objetivos.stream()
                .map(objetivoWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarObjetivo(@PathVariable UUID id) {
        deletarObjetivoInputPort.execute(id);
        return ResponseEntity.noContent().build();
    }

}

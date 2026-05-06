package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.objetivo.ObjetivoRequest;
import br.com.contadin.application.dto.objetivo.ObjetivoResponse;
import br.com.contadin.application.exception.objetivo.ObjetivoInvalidoException;
import br.com.contadin.application.port.in.objetivo.AtualizarObjetivoInputPort;
import br.com.contadin.application.port.in.objetivo.BuscarObjetivoInputPort;
import br.com.contadin.application.port.in.objetivo.CriarObjetivoInputPort;
import br.com.contadin.application.port.in.objetivo.DeletarObjetivoInputPort;
import br.com.contadin.domain.enums.TipoObjetivo;
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
    public ResponseEntity<ObjetivoResponse> criarObjetivo(@Valid @RequestBody ObjetivoRequest request) {
        Objetivo objetivo = objetivoWebMapper.toDomain(request);
        Objetivo criado = criarObjetivoInputPort.execute(objetivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(objetivoWebMapper.toResponse(criado));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ObjetivoResponse> atualizarObjetivo(
            @PathVariable UUID id,
            @RequestBody ObjetivoRequest request) {
        Objetivo objetivo = objetivoWebMapper.toDomain(request);
        Objetivo atualizado = atualizarObjetivoInputPort.execute(id, objetivo);
        return ResponseEntity.ok(objetivoWebMapper.toResponse(atualizado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjetivoResponse> buscarObjetivoPorId(@PathVariable UUID id) {
        Objetivo objetivo = buscarObjetivoInputPort.executeBuscarPorId(id);
        return ResponseEntity.ok(objetivoWebMapper.toResponse(objetivo));
    }

    @GetMapping
    public ResponseEntity<List<ObjetivoResponse>> buscarObjetivoPorUsuario(
            @RequestParam UUID fkUsuario,
            @RequestParam(required = false) Boolean concluido,
            @RequestParam(required = false) String tipo) {
        TipoObjetivo tipoObjetivo = mapTipoObjetivo(tipo);

        List<ObjetivoResponse> response = buscarObjetivoInputPort.execute(fkUsuario, concluido, tipoObjetivo).stream()
                .map(objetivoWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nome")
    public ResponseEntity<List<ObjetivoResponse>> buscarObjetivoPorNome(
            @RequestParam String nome,
            @RequestParam UUID fkUsuario) {
        List<ObjetivoResponse> response = buscarObjetivoInputPort.executeBuscarPorNome(nome, fkUsuario).stream()
                .map(objetivoWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarObjetivo(@PathVariable UUID id) {
        deletarObjetivoInputPort.execute(id);
        return ResponseEntity.noContent().build();
    }

    private TipoObjetivo mapTipoObjetivo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return null;
        }

        String valorNormalizado = tipo.trim().toUpperCase();
        return switch (valorNormalizado) {
            case "AUMENTAR", "AUMENTO_RECEITA" -> TipoObjetivo.AUMENTO_RECEITA;
            case "DIMINUIR", "LIMITE_GASTO" -> TipoObjetivo.LIMITE_GASTO;
            default -> throw new ObjetivoInvalidoException("Tipo de objetivo inválido: " + tipo);
        };
    }
}

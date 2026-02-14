package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.categoria.CategoriaRequest;
import br.com.contadin.application.port.in.CriarCategoriaInputPort;
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

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody CategoriaRequest request) {
        criarCategoriaInputPort.execute(request);
        return ResponseEntity.ok().build();
    }
}
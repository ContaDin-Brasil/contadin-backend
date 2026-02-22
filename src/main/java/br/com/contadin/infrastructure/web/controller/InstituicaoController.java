package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.instituicao.InstituicaoRequest;
import br.com.contadin.application.dto.instituicao.InstituicaoResponse;
import br.com.contadin.application.port.in.instituicao.AtualizarInstituicaoInputPort;
import br.com.contadin.application.port.in.instituicao.BuscarInstituicaoInputPort;
import br.com.contadin.application.port.in.instituicao.CriarInstituicaoInputPort;
import br.com.contadin.application.port.in.instituicao.DesativarInstituicaoInputPort;
import br.com.contadin.domain.model.Instituicao;
import br.com.contadin.infrastructure.web.mapper.InstituicaoWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/instituicoes")
public class InstituicaoController {

    private final CriarInstituicaoInputPort criarInstituicaoInputPort;
    private final AtualizarInstituicaoInputPort atualizarInstituicaoInputPort;
    private final BuscarInstituicaoInputPort buscarInstituicaoInputPort;
    private final DesativarInstituicaoInputPort desativarInstituicaoInputPort;

    private final InstituicaoWebMapper instituicaoWebMapper;

    @PostMapping
    public ResponseEntity<InstituicaoResponse> criarInstituicao(
            @Valid @RequestBody InstituicaoRequest request
    ) {

        Instituicao instituicao = instituicaoWebMapper.toDomain(request);

        Instituicao instituicaoCriada = criarInstituicaoInputPort.execute(instituicao);

        InstituicaoResponse response =
                instituicaoWebMapper.toResponse(instituicaoCriada);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

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
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/{id}")
    public ResponseEntity<InstituicaoResponse> atualizarInstituicao( @PathVariable Integer id, @Valid @RequestBody InstituicaoRequest request){

        Instituicao instituicao = instituicaoWebMapper.toDomain(request);

        Instituicao instituicaoCriada = atualizarInstituicaoInputPort.execute(id, instituicao);

        InstituicaoResponse response =
                instituicaoWebMapper.toResponse(instituicaoCriada);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativarInstituicao(@PathVariable Integer id) {
        desativarInstituicaoInputPort.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstituicaoResponse> buscarInstituicaoPorId(@PathVariable Integer id) {
        Instituicao instituicao = buscarInstituicaoInputPort.executeBuscarPorId(id);
        InstituicaoResponse response = instituicaoWebMapper.toResponse(instituicao);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> buscarInstituicoesPorUsuario(@RequestParam Integer fkUsuario) {
        return ResponseEntity.ok(buscarInstituicaoInputPort.execute(fkUsuario)
                .stream()
                .map(instituicaoWebMapper::toResponse)
                .toList());
    }

     @GetMapping("/nome")
    public ResponseEntity<?> buscarInstituicoesPorNome(@RequestParam String nome, @RequestParam Integer fkUsuario) {
         return ResponseEntity.ok(buscarInstituicaoInputPort.executeBuscarPorNome(fkUsuario, nome)
                 .stream()
                 .map(instituicaoWebMapper::toResponse)
                 .toList());
    }
}

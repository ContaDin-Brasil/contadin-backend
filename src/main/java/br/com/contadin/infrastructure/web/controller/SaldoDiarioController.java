package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.saldo.SaldoDiarioResponse;
import br.com.contadin.application.dto.saldo.SaldoNaDataResponse;
import br.com.contadin.application.dto.saldo.SaldoPorInstituicaoResponse;
import br.com.contadin.application.port.in.saldo.BuscarSaldoDiarioInputPort;
import br.com.contadin.infrastructure.web.mapper.SaldoDiarioWebMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/saldo")
@Tag(name = "Saldo Diário", description = "Consulta de saldo diário por instituição e consolidado por usuário")
public class SaldoDiarioController {

    private final BuscarSaldoDiarioInputPort buscarSaldoDiarioInputPort;
    private final SaldoDiarioWebMapper saldoDiarioWebMapper;

    @GetMapping("/instituicao/{fkInstituicao}")
    @Operation(
            summary = "Saldo por instituição em um período",
            description = "Retorna o saldo diário de uma instituição específica para cada dia do período informado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo retornado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SaldoDiarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content)
    })
    public ResponseEntity<List<SaldoDiarioResponse>> buscarPorInstituicao(
            @Parameter(description = "UUID da instituição", required = true)
            @PathVariable UUID fkInstituicao,
            @Parameter(description = "Data inicial do período (yyyy-MM-dd)", required = true, example = "2025-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data final do período (yyyy-MM-dd)", required = true, example = "2025-05-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        return ResponseEntity.ok(
                buscarSaldoDiarioInputPort.buscarPorInstituicao(fkInstituicao, dataInicio, dataFim)
                        .stream().map(saldoDiarioWebMapper::toResponse).toList()
        );
    }

    @GetMapping("/consolidado/{fkUsuario}")
    @Operation(
            summary = "Saldo consolidado do usuário em um período",
            description = "Retorna o saldo diário consolidado (soma de todas as instituições) do usuário para cada dia do período informado. Útil para gráficos de evolução de saldo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Série de saldo consolidado retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SaldoDiarioResponse.class),
                            examples = @ExampleObject(value = """
                                    [
                                      { "data": "2025-05-01", "saldoInicial": 0.00, "totalReceitas": 3500.00, "totalGastos": 1200.00, "saldoFinal": 2300.00 },
                                      { "data": "2025-05-02", "saldoInicial": 2300.00, "totalReceitas": 0.00, "totalGastos": 89.90, "saldoFinal": 2210.10 }
                                    ]
                                    """))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content)
    })
    public ResponseEntity<List<SaldoDiarioResponse>> buscarConsolidado(
            @Parameter(description = "UUID do usuário", required = true)
            @PathVariable UUID fkUsuario,
            @Parameter(description = "Data inicial do período (yyyy-MM-dd)", required = true, example = "2025-05-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data final do período (yyyy-MM-dd)", required = true, example = "2025-05-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        return ResponseEntity.ok(
                buscarSaldoDiarioInputPort.buscarConsolidado(fkUsuario, dataInicio, dataFim)
                        .stream().map(saldoDiarioWebMapper::toResponse).toList()
        );
    }

    @GetMapping("/usuario/{fkUsuario}")
    @Operation(
            summary = "Saldo total do usuário em uma data",
            description = """
                    Retorna o saldo consolidado do usuário em um dia específico,
                    somando o saldo final de todas as suas instituições (banco + vale) naquela data.
                    Se não houver registro de saldo para a data informada (ex.: antes da primeira transação),
                    retorna zeros.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Saldo do usuário na data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SaldoNaDataResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "data": "2025-05-15",
                                              "saldoTotal": 1500.00,
                                              "totalReceitas": 3500.00,
                                              "totalGastos": 2000.00
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content)
            }
    )
    public ResponseEntity<SaldoNaDataResponse> buscarSaldoNaData(
            @Parameter(description = "UUID do usuário", required = true)
            @PathVariable UUID fkUsuario,
            @Parameter(description = "Data de referência (yyyy-MM-dd)", required = true, example = "2025-05-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        return ResponseEntity.ok(buscarSaldoDiarioInputPort.buscarSaldoNaData(fkUsuario, data));
    }

    @GetMapping("/instituicoes")
    @Operation(
            summary = "Saldo por instituição em uma data",
            description = """
                    Retorna o saldo de cada instituição do usuário em um dia específico.
                    Se o parâmetro `fkInstituicao` for informado, filtra apenas aquela instituição.
                    Caso contrário, retorna todas as instituições do usuário.
                    Cada item da lista corresponde a uma instituição e inclui seu UUID para
                    que o frontend possa cruzar com nome, cor e ícone.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Saldo por instituição retornado com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SaldoPorInstituicaoResponse.class),
                                    examples = @ExampleObject(value = """
                                            [
                                              {
                                                "fkInstituicao": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                                "data": "2025-05-15",
                                                "saldoFinal": 1200.00,
                                                "totalReceitas": 3500.00,
                                                "totalGastos": 2300.00
                                              },
                                              {
                                                "fkInstituicao": "7cb91a23-1234-4321-a1b2-9f8e7d6c5b4a",
                                                "data": "2025-05-15",
                                                "saldoFinal": 300.00,
                                                "totalReceitas": 800.00,
                                                "totalGastos": 500.00
                                              }
                                            ]
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content)
            }
    )
    public ResponseEntity<List<SaldoPorInstituicaoResponse>> buscarSaldoPorInstituicoesNaData(
            @Parameter(description = "UUID do usuário", required = true)
            @RequestParam UUID fkUsuario,
            @Parameter(description = "Data de referência (yyyy-MM-dd)", required = true, example = "2025-05-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @Parameter(description = "UUID da instituição (opcional). Se omitido, retorna todas as instituições do usuário.")
            @RequestParam(required = false) UUID fkInstituicao
    ) {
        return ResponseEntity.ok(
                buscarSaldoDiarioInputPort.buscarSaldoPorInstituicoesNaData(fkUsuario, fkInstituicao, data)
        );
    }
}

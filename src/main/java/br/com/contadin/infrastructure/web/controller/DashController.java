package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.dashboard.DashReceitaGastoResponse;
import br.com.contadin.application.port.in.dashboard.DashQueryInputPort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.UUID;

@RestController
@RequestMapping("/dashboard")
public class DashController {

        private final DashQueryInputPort dashQueryInputPort;

        public DashController(
                DashQueryInputPort dashQueryInputPort
        ) {
            this.dashQueryInputPort = dashQueryInputPort;
        }

        @GetMapping("/receita")
        public DashReceitaGastoResponse buscarReceita(
                @RequestParam(required = false)
                YearMonth periodo,
                UUID usuarioId
        ) {

            YearMonth periodoConsulta =
                    periodo != null
                            ? periodo
                            : YearMonth.now();

            return dashQueryInputPort
                    .buscarReceitasPorPeriodo(periodoConsulta, usuarioId);
        }

        @GetMapping("/gasto")
        public DashReceitaGastoResponse buscarGasto(
                @RequestParam(required = false)
                YearMonth periodo,
                UUID usuarioId
        ) {

            YearMonth periodoConsulta =
                    periodo != null
                            ? periodo
                            : YearMonth.now();

            return dashQueryInputPort
                    .buscarGastosPorPeriodo(periodoConsulta, usuarioId);
        }
}

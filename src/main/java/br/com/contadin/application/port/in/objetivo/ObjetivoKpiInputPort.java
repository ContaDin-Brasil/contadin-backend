package br.com.contadin.application.port.in.objetivo;

import br.com.contadin.application.dto.objetivo.kpi.ImpactoPrevistoKpiResponse;
import br.com.contadin.application.dto.objetivo.kpi.MaiorAlertaKpiResponse;
import br.com.contadin.application.dto.objetivo.kpi.NoRitmoKpiResponse;
import br.com.contadin.domain.enums.TipoObjetivo;

import java.time.LocalDate;
import java.util.UUID;

public interface ObjetivoKpiInputPort {

    ImpactoPrevistoKpiResponse impactoPrevisto(UUID fkUsuario, LocalDate dataInicio, LocalDate dataFim, TipoObjetivo tipo);

    NoRitmoKpiResponse noRitmo(UUID fkUsuario, LocalDate dataInicio, LocalDate dataFim, TipoObjetivo tipo);

    MaiorAlertaKpiResponse maiorAlerta(UUID fkUsuario, LocalDate dataInicio, LocalDate dataFim, TipoObjetivo tipo);
}

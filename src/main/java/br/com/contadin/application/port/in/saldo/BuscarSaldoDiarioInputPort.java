package br.com.contadin.application.port.in.saldo;

import br.com.contadin.application.dto.saldo.SaldoNaDataResponse;
import br.com.contadin.application.dto.saldo.SaldoPorInstituicaoResponse;
import br.com.contadin.domain.model.SaldoDiario;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BuscarSaldoDiarioInputPort {
    List<SaldoDiario> buscarPorInstituicao(UUID fkInstituicao, LocalDate inicio, LocalDate fim);
    List<SaldoDiario> buscarConsolidado(UUID fkUsuario, LocalDate inicio, LocalDate fim);
    SaldoNaDataResponse buscarSaldoNaData(UUID fkUsuario, LocalDate data);
    List<SaldoPorInstituicaoResponse> buscarSaldoPorInstituicoesNaData(UUID fkUsuario, UUID fkInstituicao, LocalDate data);
}

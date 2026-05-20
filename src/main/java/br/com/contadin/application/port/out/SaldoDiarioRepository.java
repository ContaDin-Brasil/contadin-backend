package br.com.contadin.application.port.out;

import br.com.contadin.domain.model.SaldoDiario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SaldoDiarioRepository {
    Optional<SaldoDiario> buscarUltimoAntesDeData(UUID fkInstituicao, LocalDate data);
    void deletarAPartirDeData(UUID fkInstituicao, LocalDate data);
    List<SaldoDiario> salvarTodos(List<SaldoDiario> saldos);
    List<SaldoDiario> buscarPorInstituicaoEPeriodo(UUID fkInstituicao, LocalDate inicio, LocalDate fim);
    List<SaldoDiario> buscarConsolidadoPorUsuario(UUID fkUsuario, LocalDate inicio, LocalDate fim);
    List<SaldoDiario> buscarPorUsuarioNaData(UUID fkUsuario, LocalDate data);
    List<SaldoDiario> buscarPorInstituicaoNaData(UUID fkInstituicao, LocalDate data);
}

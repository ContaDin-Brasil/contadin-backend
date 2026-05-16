package br.com.contadin.application.usecase.saldo;

import br.com.contadin.application.dto.saldo.SaldoNaDataResponse;
import br.com.contadin.application.dto.saldo.SaldoPorInstituicaoResponse;
import br.com.contadin.application.port.in.saldo.BuscarSaldoDiarioInputPort;
import br.com.contadin.application.port.out.SaldoDiarioRepository;
import br.com.contadin.domain.model.SaldoDiario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BuscarSaldoDiarioUseCase implements BuscarSaldoDiarioInputPort {

    private final SaldoDiarioRepository saldoDiarioRepository;

    @Override
    public List<SaldoDiario> buscarPorInstituicao(UUID fkInstituicao, LocalDate inicio, LocalDate fim) {
        return saldoDiarioRepository.buscarPorInstituicaoEPeriodo(fkInstituicao, inicio, fim);
    }

    @Override
    public List<SaldoDiario> buscarConsolidado(UUID fkUsuario, LocalDate inicio, LocalDate fim) {
        return saldoDiarioRepository.buscarConsolidadoPorUsuario(fkUsuario, inicio, fim);
    }

    @Override
    public SaldoNaDataResponse buscarSaldoNaData(UUID fkUsuario, LocalDate data) {
        List<SaldoDiario> resultados = saldoDiarioRepository.buscarConsolidadoPorUsuario(fkUsuario, data, data);

        if (resultados.isEmpty()) {
            return new SaldoNaDataResponse(data, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        SaldoDiario consolidado = resultados.get(0);
        return new SaldoNaDataResponse(
                consolidado.getData(),
                consolidado.getSaldoFinal(),
                consolidado.getTotalReceitas(),
                consolidado.getTotalGastos()
        );
    }

    @Override
    public List<SaldoPorInstituicaoResponse> buscarSaldoPorInstituicoesNaData(UUID fkUsuario, UUID fkInstituicao, LocalDate data) {
        List<SaldoDiario> saldos = fkInstituicao != null
                ? saldoDiarioRepository.buscarPorInstituicaoNaData(fkInstituicao, data)
                : saldoDiarioRepository.buscarPorUsuarioNaData(fkUsuario, data);

        return saldos.stream()
                .map(s -> new SaldoPorInstituicaoResponse(
                        s.getFkInstituicao(),
                        s.getData(),
                        s.getSaldoFinal(),
                        s.getTotalReceitas(),
                        s.getTotalGastos()
                ))
                .toList();
    }
}

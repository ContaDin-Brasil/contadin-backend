package br.com.contadin.application.usecase.objetivo;

import br.com.contadin.application.exception.objetivo.ObjetivoInvalidoException;
import br.com.contadin.application.exception.objetivo.ObjetivoNaoEncontradoException;
import br.com.contadin.application.port.in.objetivo.BuscarObjetivoInputPort;
import br.com.contadin.application.usecase.objetivo.helper.ObjetivoMetricasHelper;
import br.com.contadin.application.port.out.ObjetivoRepository;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.model.Objetivo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarObjetivoUseCase implements BuscarObjetivoInputPort {

    private final ObjetivoRepository objetivoRepository;
    private final TransacaoRepository transacaoRepository;

    @Override
    public List<Objetivo> execute(UUID fkUsuario) {
        return execute(fkUsuario, null);
    }

    @Override
    public List<Objetivo> execute(UUID fkUsuario, Boolean concluido) {
        if (fkUsuario == null) {
            throw new ObjetivoInvalidoException("ID do usuário é obrigatório para busca");
        }
        LocalDate hoje = LocalDate.now();

        return objetivoRepository.findByUsuario(fkUsuario).stream()
                .map(o -> ObjetivoMetricasHelper.calcular(o, transacaoRepository))
                .filter(o -> {
                    if (concluido == null) {
                        return true;
                    }
                    boolean objetivoConcluido = o.getDataFim() != null && o.getDataFim().isBefore(hoje);
                    return concluido.equals(objetivoConcluido);
                })
                .toList();
    }

    @Override
    public Objetivo executeBuscarPorId(UUID objetivoId) {
        if (objetivoId == null) {
            throw new ObjetivoInvalidoException("ID do objetivo é obrigatório para busca");
        }
        Objetivo objetivo = objetivoRepository.findById(objetivoId)
                .orElseThrow(ObjetivoNaoEncontradoException::new);
        return ObjetivoMetricasHelper.calcular(objetivo, transacaoRepository);
    }

    @Override
    public List<Objetivo> executeBuscarPorNome(String nome, UUID fkUsuario, Boolean concluido) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ObjetivoInvalidoException("Nome do objetivo é obrigatório para busca");
        }
        if (fkUsuario == null) {
            throw new ObjetivoInvalidoException("ID do usuário é obrigatório para busca");
        }

        LocalDate hoje = LocalDate.now();

        return objetivoRepository.findByNomeAndUsuario(nome, fkUsuario).stream()
                .map(o -> ObjetivoMetricasHelper.calcular(o, transacaoRepository))
                .filter(o -> {
                    if (concluido == null) {
                        return true;
                    }
                    boolean objetivoConcluido = o.getDataFim() != null && o.getDataFim().isBefore(hoje);
                    return concluido.equals(objetivoConcluido);
                })
                .toList();
    }
}

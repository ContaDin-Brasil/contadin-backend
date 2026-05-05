package br.com.contadin.application.usecase.objetivo;

import br.com.contadin.application.exception.objetivo.ObjetivoInvalidoException;
import br.com.contadin.application.port.in.objetivo.CriarObjetivoInputPort;
import br.com.contadin.application.usecase.objetivo.helper.ObjetivoMetricasHelper;
import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.application.port.out.ObjetivoRepository;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.enums.TipoCategoria;
import br.com.contadin.domain.enums.TipoObjetivo;
import br.com.contadin.domain.model.Categoria;
import br.com.contadin.domain.model.Objetivo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CriarObjetivoUseCase implements CriarObjetivoInputPort {

    private final ObjetivoRepository objetivoRepository;
    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public Objetivo execute(Objetivo objetivo) {
        validar(objetivo);

        LocalDate dataInicio = objetivo.getDataInicio() != null
                ? objetivo.getDataInicio()
                : LocalDate.now();

        Objetivo toSave = Objetivo.builder()
                .nome(objetivo.getNome())
                .descricao(objetivo.getDescricao())
                .valor(objetivo.getValor())
                .tipoObjetivo(objetivo.getTipoObjetivo())
                .prioridade(objetivo.getPrioridade())
                .dataInicio(dataInicio)
                .dataFim(objetivo.getDataFim())
                .fkUsuario(objetivo.getFkUsuario())
                .fkCategoria(objetivo.getFkCategoria())
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        Objetivo salvo = objetivoRepository.save(toSave);
        return ObjetivoMetricasHelper.calcular(salvo, transacaoRepository);
    }

    private void validar(Objetivo objetivo) {
        if (objetivo.getValor() == null || objetivo.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ObjetivoInvalidoException("Valor deve ser positivo");
        }
        if (objetivo.getDataFim() != null && objetivo.getDataInicio() != null
                && objetivo.getDataFim().isBefore(objetivo.getDataInicio())) {
            throw new ObjetivoInvalidoException("Data fim deve ser igual ou posterior à data início");
        }
        validarCategoriaCoerente(objetivo);
    }

    private void validarCategoriaCoerente(Objetivo objetivo) {
        Categoria categoria = categoriaRepository.findById(objetivo.getFkCategoria())
                .orElseThrow(() -> new ObjetivoInvalidoException("Categoria não encontrada"));

        TipoCategoria esperado = objetivo.getTipoObjetivo() == TipoObjetivo.LIMITE_GASTO
                ? TipoCategoria.GASTO
                : TipoCategoria.RECEITA;

        if (categoria.getTipo() != TipoCategoria.GLOBAL && categoria.getTipo() != esperado) {
            throw new ObjetivoInvalidoException(
                    "Categoria do tipo " + categoria.getTipo() + " é incompatível com objetivo " + objetivo.getTipoObjetivo());
        }
    }
}

package br.com.contadin.application.usecase.objetivo;

import br.com.contadin.application.exception.objetivo.ObjetivoInvalidoException;
import br.com.contadin.application.exception.objetivo.ObjetivoNaoEncontradoException;
import br.com.contadin.application.port.in.objetivo.AtualizarObjetivoInputPort;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtualizarObjetivoUseCase implements AtualizarObjetivoInputPort {

    private final ObjetivoRepository objetivoRepository;
    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public Objetivo execute(UUID id, Objetivo patch) {
        if (id == null) {
            throw new ObjetivoInvalidoException("ID do objetivo é obrigatório para atualização");
        }

        Objetivo existente = objetivoRepository.findById(id)
                .orElseThrow(ObjetivoNaoEncontradoException::new);

        TipoObjetivo tipoFinal = patch.getTipoObjetivo() != null ? patch.getTipoObjetivo() : existente.getTipoObjetivo();
        UUID categoriaFinal = patch.getFkCategoria() != null ? patch.getFkCategoria() : existente.getFkCategoria();
        LocalDate dataInicioFinal = patch.getDataInicio() != null ? patch.getDataInicio() : existente.getDataInicio();
        LocalDate dataFimFinal = patch.getDataFim() != null ? patch.getDataFim() : existente.getDataFim();

        if (dataFimFinal.isBefore(dataInicioFinal)) {
            throw new ObjetivoInvalidoException("Data fim deve ser igual ou posterior à data início");
        }

        if (patch.getFkCategoria() != null || patch.getTipoObjetivo() != null) {
            validarCategoriaCoerente(tipoFinal, categoriaFinal);
        }

        Objetivo toSave = Objetivo.builder()
                .id(existente.getId())
                .nome(patch.getNome() != null ? patch.getNome() : existente.getNome())
                .descricao(patch.getDescricao() != null ? patch.getDescricao() : existente.getDescricao())
                .valor(patch.getValor() != null ? patch.getValor() : existente.getValor())
                .tipoObjetivo(tipoFinal)
                .prioridade(patch.getPrioridade() != null ? patch.getPrioridade() : existente.getPrioridade())
                .dataInicio(dataInicioFinal)
                .dataFim(dataFimFinal)
                .fkUsuario(existente.getFkUsuario())
                .fkCategoria(categoriaFinal)
                .criadoEm(existente.getCriadoEm())
                .atualizadoEm(LocalDateTime.now())
                .build();

        Objetivo salvo = objetivoRepository.save(toSave);
        return ObjetivoMetricasHelper.calcular(salvo, transacaoRepository);
    }

    private void validarCategoriaCoerente(TipoObjetivo tipo, UUID fkCategoria) {
        Categoria categoria = categoriaRepository.findById(fkCategoria)
                .orElseThrow(() -> new ObjetivoInvalidoException("Categoria não encontrada"));

        TipoCategoria esperado = tipo == TipoObjetivo.LIMITE_GASTO
                ? TipoCategoria.GASTO
                : TipoCategoria.RECEITA;

        if (categoria.getTipo() != TipoCategoria.GLOBAL && categoria.getTipo() != esperado) {
            throw new ObjetivoInvalidoException(
                    "Categoria do tipo " + categoria.getTipo() + " é incompatível com objetivo " + tipo);
        }
    }
}

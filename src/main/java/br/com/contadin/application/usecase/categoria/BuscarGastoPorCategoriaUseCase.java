package br.com.contadin.application.usecase.categoria;

import br.com.contadin.application.dto.categoria.GastoPorCategoriaResponse;
import br.com.contadin.application.dto.transacao.GastoCategoriaAgregado;
import br.com.contadin.application.port.in.categoria.BuscarGastoPorCategoriaInputPort;
import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.model.Categoria;
import br.com.contadin.domain.model.Instituicao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarGastoPorCategoriaUseCase implements BuscarGastoPorCategoriaInputPort {

    private final TransacaoRepository transacaoRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public List<GastoPorCategoriaResponse> execute(UUID fkUsuario, int mes, int ano, UUID fkInstituicao) {
        List<UUID> instituicaoIds = resolverInstituicoes(fkUsuario, fkInstituicao);
        if (instituicaoIds.isEmpty()) {
            return List.of();
        }

        LocalDate primeiroDia = LocalDate.of(ano, mes, 1);
        LocalDateTime inicio = primeiroDia.atStartOfDay();
        LocalDateTime fim = primeiroDia.plusMonths(1).atStartOfDay();

        List<GastoCategoriaAgregado> agregados = transacaoRepository.buscarGastoPorCategoria(instituicaoIds, inicio, fim);
        if (agregados.isEmpty()) {
            return List.of();
        }

        BigDecimal totalGeral = agregados.stream()
                .map(GastoCategoriaAgregado::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return agregados.stream()
                .map(a -> enricher(a, totalGeral))
                .toList();
    }

    private List<UUID> resolverInstituicoes(UUID fkUsuario, UUID fkInstituicao) {
        if (fkInstituicao != null) {
            return List.of(fkInstituicao);
        }
        return instituicaoRepository.findAtivasByUsuario(fkUsuario)
                .stream().map(Instituicao::getId).toList();
    }

    private GastoPorCategoriaResponse enricher(GastoCategoriaAgregado agregado, BigDecimal totalGeral) {
        Categoria cat = agregado.fkCategoria() != null
                ? categoriaRepository.findByIdIncludingInactive(agregado.fkCategoria()).orElse(null)
                : null;

        BigDecimal percentual = totalGeral.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : agregado.total()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalGeral, 2, RoundingMode.HALF_UP);

        return new GastoPorCategoriaResponse(
                agregado.fkCategoria(),
                cat != null ? cat.getNome() : "Sem Categoria",
                cat != null ? cat.getIcone() : null,
                cat != null ? cat.getCor() : null,
                agregado.total(),
                percentual
        );
    }
}

package br.com.contadin.application.usecase.transacao;

import br.com.contadin.application.dto.transacao.TransacaoConsultaParams;
import br.com.contadin.application.dto.transacao.TransacaoFiltro;
import br.com.contadin.application.port.in.transacao.BuscarTransacaoInputPort;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.exception.transacao.TransacaoInvalidaException;
import br.com.contadin.domain.exception.transacao.TransacaoNaoEncontradaException;
import br.com.contadin.domain.model.Transacao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarTransacaoUseCase implements BuscarTransacaoInputPort {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_LIMIT = 10;
    private static final String DEFAULT_SORT = "data_transacao";
    private static final String DEFAULT_ORDER = "desc";
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("data_transacao", "valor", "descricao");
    private static final Map<String, String> SORT_MAPPING = Map.of(
            "data_transacao", "dataTransacao",
            "valor", "valor",
            "descricao", "descricao"
    );

    private final TransacaoRepository transacaoRepository;

    @Override
    public List<Transacao> execute() {
        return transacaoRepository.findAll();
    }

    @Override
    public Page<Transacao> execute(TransacaoConsultaParams params) {
        int page = resolvePage(params.page());
        int limit = resolveLimit(params.limit());
        String sortField = resolveSortField(params.sort());
        Sort.Direction direction = resolveDirection(params.order());

        LocalDateTime dataTransacaoGte = parseDateStart(params.dataTransacaoGte(), "data_transacao_gte");
        LocalDateTime dataTransacaoLte = parseDateEnd(params.dataTransacaoLte(), "data_transacao_lte");

        if (params.valorGte() != null && params.valorLte() != null && params.valorGte() > params.valorLte()) {
            throw new TransacaoInvalidaException("valor_gte não pode ser maior que valor_lte.");
        }

        if (dataTransacaoGte != null && dataTransacaoLte != null && dataTransacaoGte.isAfter(dataTransacaoLte)) {
            throw new TransacaoInvalidaException("data_transacao_gte não pode ser maior que data_transacao_lte.");
        }

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(direction, sortField));

        TransacaoFiltro filtro = new TransacaoFiltro(
                params.tipo(),
                params.fkInstituicao(),
                params.fkCategoria(),
                params.valorGte(),
                params.valorLte(),
                params.parcelado(),
                params.recorrente(),
                dataTransacaoGte,
                dataTransacaoLte,
                normalizeSearch(params.search())
        );

        return transacaoRepository.findAll(filtro, pageable);
    }

    @Override
    public Transacao executeBuscarPorId(UUID transacaoId) {
        if (transacaoId == null) {
            throw new TransacaoInvalidaException("ID da transação é obrigatório para busca.");
        }

        return transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> new TransacaoNaoEncontradaException("Transação não encontrada."));
    }

    private int resolvePage(Integer page) {
        int resolved = page == null ? DEFAULT_PAGE : page;
        if (resolved < 1) {
            throw new TransacaoInvalidaException("_page deve ser maior ou igual a 1.");
        }
        return resolved;
    }

    private int resolveLimit(Integer limit) {
        int resolved = limit == null ? DEFAULT_LIMIT : limit;
        if (resolved < 1) {
            throw new TransacaoInvalidaException("_limit deve ser maior ou igual a 1.");
        }
        return resolved;
    }

    private String resolveSortField(String sort) {
        String resolved = sort == null || sort.isBlank() ? DEFAULT_SORT : sort;

        if (!ALLOWED_SORT_FIELDS.contains(resolved)) {
            throw new TransacaoInvalidaException("_sort inválido. Valores permitidos: data_transacao, valor, descricao.");
        }

        return SORT_MAPPING.get(resolved);
    }

    private Sort.Direction resolveDirection(String order) {
        String resolved = order == null || order.isBlank() ? DEFAULT_ORDER : order.toLowerCase(Locale.ROOT);

        if (!"asc".equals(resolved) && !"desc".equals(resolved)) {
            throw new TransacaoInvalidaException("_order inválido. Valores permitidos: asc, desc.");
        }

        return Sort.Direction.fromString(resolved);
    }

    private LocalDateTime parseDateStart(String rawDate, String paramName) {
        if (rawDate == null || rawDate.isBlank()) {
            return null;
        }

        try {
            return LocalDateTime.parse(rawDate);
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDate.parse(rawDate).atStartOfDay();
        } catch (DateTimeParseException ex) {
            throw new TransacaoInvalidaException(paramName + " inválido. Use yyyy-MM-dd ou yyyy-MM-dd'T'HH:mm:ss.");
        }
    }

    private LocalDateTime parseDateEnd(String rawDate, String paramName) {
        if (rawDate == null || rawDate.isBlank()) {
            return null;
        }

        try {
            return LocalDateTime.parse(rawDate);
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDate.parse(rawDate).atTime(23, 59, 59);
        } catch (DateTimeParseException ex) {
            throw new TransacaoInvalidaException(paramName + " inválido. Use yyyy-MM-dd ou yyyy-MM-dd'T'HH:mm:ss.");
        }
    }

    private String normalizeSearch(String search) {
        if (search == null) {
            return null;
        }

        String normalized = search.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}

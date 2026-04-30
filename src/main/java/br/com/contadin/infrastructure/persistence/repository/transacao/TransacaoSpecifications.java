package br.com.contadin.infrastructure.persistence.repository.transacao;

import br.com.contadin.application.dto.transacao.TransacaoFiltro;
import br.com.contadin.infrastructure.persistence.entity.TransacaoEntity;
import org.springframework.data.jpa.domain.Specification;

public final class TransacaoSpecifications {

    private TransacaoSpecifications() {
    }

    public static Specification<TransacaoEntity> withFilters(TransacaoFiltro filtro) {
        return (root, query, cb) -> {
            Specification<TransacaoEntity> spec = Specification.where(null);

            if (filtro == null) {
                return spec.toPredicate(root, query, cb);
            }

            if (filtro.tipo() != null) {
                spec = spec.and((r, q, builder) -> builder.equal(r.get("tipo"), filtro.tipo()));
            }

            if (filtro.fkInstituicao() != null) {
                spec = spec.and((r, q, builder) -> builder.equal(r.get("fkInstituicao"), filtro.fkInstituicao()));
            }

            if (filtro.fkCategoria() != null) {
                spec = spec.and((r, q, builder) -> builder.equal(r.get("fkCategoria"), filtro.fkCategoria()));
            }

            if (filtro.valorGte() != null) {
                spec = spec.and((r, q, builder) -> builder.greaterThanOrEqualTo(r.get("valor"), filtro.valorGte()));
            }

            if (filtro.valorLte() != null) {
                spec = spec.and((r, q, builder) -> builder.lessThanOrEqualTo(r.get("valor"), filtro.valorLte()));
            }

            if (filtro.parcelado() != null) {
                spec = spec.and((r, q, builder) -> builder.equal(r.get("parcelado"), filtro.parcelado()));
            }

            if (filtro.recorrente() != null) {
                if (Boolean.TRUE.equals(filtro.recorrente())) {
                    spec = spec.and((r, q, builder) -> builder.isNotNull(r.get("recorrencia")));
                } else {
                    spec = spec.and((r, q, builder) -> builder.isNull(r.get("recorrencia")));
                }
            }

            if (filtro.dataTransacaoGte() != null) {
                spec = spec.and((r, q, builder) -> builder.greaterThanOrEqualTo(r.get("dataTransacao"), filtro.dataTransacaoGte()));
            }

            if (filtro.dataTransacaoLte() != null) {
                spec = spec.and((r, q, builder) -> builder.lessThanOrEqualTo(r.get("dataTransacao"), filtro.dataTransacaoLte()));
            }

            if (filtro.search() != null && !filtro.search().isBlank()) {
                String term = "%" + filtro.search().trim().toLowerCase() + "%";
                spec = spec.and((r, q, builder) -> builder.like(builder.lower(r.get("descricao")), term));
            }

            return spec.toPredicate(root, query, cb);
        };
    }
}

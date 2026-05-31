package br.com.contadin.application.dto.objetivo;

import br.com.contadin.domain.enums.PrioridadeObjetivo;
import br.com.contadin.domain.enums.TipoObjetivo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ObjetivoRequest(
        @NotNull(message = "Tipo do objetivo é obrigatório")
        TipoObjetivo tipoObjetivo,

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        String descricao,

        @NotNull(message = "Valor é obrigatório")
        @Positive(message = "Valor deve ser positivo")
        BigDecimal valor,

        LocalDate dataInicio,

        @NotNull(message = "Data fim é obrigatória")
        LocalDate dataFim,

        PrioridadeObjetivo prioridade,

        @NotNull(message = "Categoria é obrigatória")
        UUID fkCategoria,

        @NotNull(message = "Usuário é obrigatório")
        UUID fkUsuario
) {}

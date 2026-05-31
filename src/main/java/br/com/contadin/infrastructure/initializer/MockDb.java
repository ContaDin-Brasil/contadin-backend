package br.com.contadin.infrastructure.initializer;

import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.application.port.out.ObjetivoRepository;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.application.service.SaldoDiarioCalculadorService;
import br.com.contadin.domain.enums.*;
import br.com.contadin.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class MockDb {

    private final InstituicaoRepository instituicaoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ObjetivoRepository objetivoRepository;
    private final TransacaoRepository transacaoRepository;
    private final SaldoDiarioCalculadorService saldoDiarioCalculadorService;

    @Value("${app.set.mockdata:false}")
    private Boolean setMockData;

    public void initializeMockData(UUID usuarioId) {
        if (!setMockData) {
            log.info("Aplicação iniciando sem dados mocados");
            return;
        }

        if (!instituicaoRepository.findAtivasByUsuario(usuarioId).isEmpty()) {
            log.info("Dados mock já existem; pulando inicialização.");
            return;
        }

        // ===========
        // -- Mocks --
        // ===========

        // Instituicoes

        List<Instituicao> instituicoesParaSalvar = List.of(
                Instituicao.builder()
                        .nome("Nubank")
                        .icone("nubank")
                        .cor("#820AD1")
                        .tipo(TipoInstituicao.BANCO)
                        .fkUsuario(usuarioId)
                        .saldoInicial(BigDecimal.ZERO)
                        .ativo(true)
                        .criadoEm(LocalDateTime.now())
                        .atualizadoEm(LocalDateTime.now())
                        .build(),
                Instituicao.builder()
                        .nome("Vale Alimentação")
                        .icone("vale")
                        .cor("#00A86B")
                        .tipo(TipoInstituicao.VALE)
                        .fkUsuario(usuarioId)
                        .saldoInicial(BigDecimal.ZERO)
                        .ativo(true)
                        .criadoEm(LocalDateTime.now())
                        .atualizadoEm(LocalDateTime.now())
                        .build()
        );

        List<UUID> instituicaoIds = new ArrayList<>();
        for (Instituicao instituicao : instituicoesParaSalvar) {
            Instituicao saved = instituicaoRepository.save(instituicao);
            instituicaoIds.add(saved.getId());
        }
        log.info("Instituicoes cadastradas com sucesso!");

        // Categorias

        List<Categoria> categoriasParaSalvar = List.of(
                Categoria.builder()
                        .nome("Alimentação")
                        .icone("restaurant")
                        .cor("#FF6B6B")
                        .tipo(TipoCategoria.GASTO).build(),
                Categoria.builder()
                        .nome("Transporte")
                        .icone("directions-car")
                        .cor("#4ECDC4")
                        .tipo(TipoCategoria.GASTO).build(),
                Categoria.builder()
                        .nome("Lazer")
                        .icone("sports-esports")
                        .cor("#FDCB6E")
                        .tipo(TipoCategoria.GASTO).build(),
                Categoria.builder()
                        .nome("Educação")
                        .icone("school")
                        .cor("#95E1D3")
                        .tipo(TipoCategoria.GASTO).build(),
                Categoria.builder()
                        .nome("Aluguel")
                        .icone("apartment")
                        .cor("#FD79A8")
                        .tipo(TipoCategoria.GASTO).build(),
                Categoria.builder()
                        .nome("Salário")
                        .icone("attach-money")
                        .cor("#FFD700")
                        .tipo(TipoCategoria.RECEITA).build(),
                Categoria.builder()
                        .nome("Investimento")
                        .icone("trending-up")
                        .cor("#00FF00")
                        .tipo(TipoCategoria.RECEITA).build(),
                Categoria.builder()
                        .nome("Mercado")
                        .icone("local-grocery-store")
                        .cor("#FF8C00")
                        .tipo(TipoCategoria.GASTO).build()
        );

        List<UUID> categoriaIds = new ArrayList<>();
        for (Categoria categoria : categoriasParaSalvar) {
            Categoria saved = categoriaRepository.save(categoria);
            categoriaIds.add(saved.getId());
        }
        log.info("Categorias cadastradas com sucesso!");

        // Objetivos (5 cenários para validar KPIs — ver comentário no fim do método)
        // índices: 0=Alimentação, 1=Transporte, 2=Lazer, 3=Educação, 4=Aluguel, 5=Salário, 6=Investimento, 7=Mercado

        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate fimMes = inicioMes.plusMonths(1).minusDays(1);

        List<Objetivo> objetivos = List.of(
                Objetivo.builder()
                        .nome("Limitar delivery no mês")
                        .descricao("Meta R$ 600 — realizado R$ 100 (~17%, TRANQUILO, no ritmo)")
                        .valor(new BigDecimal("600.00"))
                        .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                        .prioridade(PrioridadeObjetivo.ALTA)
                        .dataInicio(inicioMes)
                        .dataFim(fimMes)
                        .fkUsuario(usuarioId)
                        .fkCategoria(categoriaIds.get(0))
                        .build(),
                Objetivo.builder()
                        .nome("Limitar transporte")
                        .descricao("Meta R$ 300 — realizado R$ 240 (~80%, CUIDADO, no ritmo)")
                        .valor(new BigDecimal("300.00"))
                        .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                        .prioridade(PrioridadeObjetivo.MEDIA)
                        .dataInicio(inicioMes)
                        .dataFim(fimMes)
                        .fkUsuario(usuarioId)
                        .fkCategoria(categoriaIds.get(1))
                        .build(),
                Objetivo.builder()
                        .nome("Teto supermercado / lazer")
                        .descricao("Meta R$ 500 — realizado R$ 620 (~124%, ACIMA_DO_COMBINADO, maior alerta)")
                        .valor(new BigDecimal("500.00"))
                        .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                        .prioridade(PrioridadeObjetivo.BAIXA)
                        .dataInicio(inicioMes)
                        .dataFim(fimMes)
                        .fkUsuario(usuarioId)
                        .fkCategoria(categoriaIds.get(2))
                        .build(),
                Objetivo.builder()
                        .nome("Renda extra com freelas")
                        .descricao("Meta R$ 400 — realizado R$ 380 (~95%, FALTA_POUCO, no ritmo)")
                        .valor(new BigDecimal("400.00"))
                        .tipoObjetivo(TipoObjetivo.AUMENTO_RECEITA)
                        .prioridade(PrioridadeObjetivo.ALTA)
                        .dataInicio(inicioMes)
                        .dataFim(fimMes)
                        .fkUsuario(usuarioId)
                        .fkCategoria(categoriaIds.get(5))
                        .build(),
                Objetivo.builder()
                        .nome("Receita com investimentos")
                        .descricao("Meta R$ 200 — realizado R$ 50 (~25%, NO_CAMINHO, fora do ritmo)")
                        .valor(new BigDecimal("200.00"))
                        .tipoObjetivo(TipoObjetivo.AUMENTO_RECEITA)
                        .dataInicio(inicioMes)
                        .dataFim(fimMes)
                        .fkUsuario(usuarioId)
                        .fkCategoria(categoriaIds.get(6))
                        .build()
        );

        for (Objetivo objetivo : objetivos) {
            objetivoRepository.save(objetivo);
        }
        log.info("Objetivos cadastrados com sucesso!");

        // Transacoes (histórico para dashboard/saldo + mês atual para KPIs de objetivos)

        Date fimRecorrencia = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(fimRecorrencia);
        cal.add(Calendar.MONTH, 12);
        fimRecorrencia = cal.getTime();

        LocalDateTime dataNoMes = inicioMes.plusDays(10).atTime(12, 0);
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime tresMesesAtras = LocalDateTime.now().minusMonths(3).withDayOfMonth(1);

        List<Transacao> transacoes = new ArrayList<>(List.of(
                // Nubank - Salário mensal recorrente (3 meses atrás até 1 ano à frente)
                Transacao.builder()
                        .valor(3500.00)
                        .tipo(TipoTransacao.RECEITA)
                        .descricao("Salário")
                        .dataTransacao(tresMesesAtras.withDayOfMonth(5))
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(3))
                        .criadoEm(agora)
                        .atualizadoEm(agora)
                        .build(),
                // Nubank - Aluguel mensal recorrente
                Transacao.builder()
                        .valor(1200.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Aluguel")
                        .dataTransacao(tresMesesAtras.withDayOfMonth(10))
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(4))
                        .criadoEm(agora)
                        .atualizadoEm(agora)
                        .build(),
                // Nubank - Supermercado pontual (mês passado)
                Transacao.builder()
                        .valor(320.75)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Supermercado")
                        .dataTransacao(LocalDateTime.now().minusMonths(1).withDayOfMonth(15))
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(0))
                        .criadoEm(agora)
                        .atualizadoEm(agora)
                        .build(),
                // Nubank - Transporte pontual (hoje)
                Transacao.builder()
                        .valor(89.90)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Transporte")
                        .dataTransacao(agora)
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(1))
                        .criadoEm(agora)
                        .atualizadoEm(agora)
                        .build(),
                // Vale - Alimentação semanal recorrente
                Transacao.builder()
                        .valor(150.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Refeições")
                        .dataTransacao(tresMesesAtras.withDayOfMonth(1))
                        .parcelado(false)
                        .recorrencia(Recorrencia.SEMANAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(1))
                        .fkCategoria(categoriaIds.get(0))
                        .criadoEm(agora)
                        .atualizadoEm(agora)
                        .build(),
                // Vale - Recarga mensal
                Transacao.builder()
                        .valor(800.00)
                        .tipo(TipoTransacao.RECEITA)
                        .descricao("Recarga Vale")
                        .dataTransacao(tresMesesAtras.withDayOfMonth(1))
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(1))
                        .fkCategoria(categoriaIds.get(3))
                        .criadoEm(agora)
                        .atualizadoEm(agora)
                        .build(),
                // KPI objetivos — mês atual
                Transacao.builder()
                        .valor(100.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Delivery — mock KPI delivery")
                        .dataTransacao(dataNoMes)
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(0))
                        .criadoEm(agora)
                        .atualizadoEm(agora)
                        .build(),
                Transacao.builder()
                        .valor(240.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Combustível e apps — mock KPI transporte")
                        .dataTransacao(dataNoMes)
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(1))
                        .criadoEm(agora)
                        .atualizadoEm(agora)
                        .build(),
                Transacao.builder()
                        .valor(620.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Compras lazer — mock KPI estouro")
                        .dataTransacao(dataNoMes)
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(2))
                        .criadoEm(agora)
                        .atualizadoEm(agora)
                        .build(),
                Transacao.builder()
                        .valor(380.00)
                        .tipo(TipoTransacao.RECEITA)
                        .descricao("Freela design — mock KPI receita")
                        .dataTransacao(dataNoMes)
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(1))
                        .fkCategoria(categoriaIds.get(5))
                        .criadoEm(agora)
                        .atualizadoEm(agora)
                        .build(),
                Transacao.builder()
                        .valor(50.00)
                        .tipo(TipoTransacao.RECEITA)
                        .descricao("Dividendos — mock KPI investimentos")
                        .dataTransacao(dataNoMes)
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(1))
                        .fkCategoria(categoriaIds.get(6))
                        .criadoEm(agora)
                        .atualizadoEm(agora)
                        .build()
        ));

        for (Transacao transacao : transacoes) {
            transacaoRepository.save(transacao);
        }
        log.info("Transacoes cadastradas com sucesso!");

        LocalDateTime dataInicioRecalculo = tresMesesAtras;
        for (UUID instId : instituicaoIds) {
            try {
                saldoDiarioCalculadorService.recalcular(instId, dataInicioRecalculo.toLocalDate());
                log.info("Saldo diário recalculado para instituição: {}", instId);
            } catch (Exception e) {
                log.warn("Falha ao recalcular saldo diário para instituição {}: {}", instId, e.getMessage());
            }
        }

        log.info("""
                Mock KPI objetivos — valores esperados (mes atual):
                  GET /objetivos/kpis/impacto-previsto  -> impactoPrevistoMes: 1370
                    (economias 500+60+380 + receitas 380+50)
                  GET /objetivos/kpis/no-ritmo           -> objetivosNoRitmo: 3, totalObjetivos: 5
                    (delivery, transporte, freelas — fora: lazer estourado, investimentos)
                  GET /objetivos/kpis/maior-alerta     -> "Lazer: 124% do limite usado"
                """);
    }
}

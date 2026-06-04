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

        // ─────────────────────────────────────────────────────────────────────
        // Instituições
        //   0 = Nubank          (banco digital — recebe salário e gastos gerais)
        //   1 = Vale Refeição   (benefício R$ 1.200/mês — almoço/refeições)
        //   2 = Vale Alimentação (benefício R$ 800/mês  — supermercado/mercado)
        // ─────────────────────────────────────────────────────────────────────

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
                        .nome("Vale Refeição")
                        .icone("vale")
                        .cor("#F59E0B")
                        .tipo(TipoInstituicao.VALE)
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

        // ─────────────────────────────────────────────────────────────────────
        // Categorias
        //   índices: 0=Alimentação, 1=Transporte, 2=Lazer,   3=Saúde,
        //            4=Moradia,     5=Salário,    6=Investimento, 7=Mercado,
        //            8=Renda Extra, 9=Educação,   10=Telecom
        // ─────────────────────────────────────────────────────────────────────

        List<Categoria> categoriasParaSalvar = List.of(
                Categoria.builder()
                        .nome("Alimentação")
                        .icone("restaurant")
                        .cor("#FF6B6B")
                        .tipo(TipoCategoria.GASTO).build(),          // 0
                Categoria.builder()
                        .nome("Transporte")
                        .icone("directions-car")
                        .cor("#4ECDC4")
                        .tipo(TipoCategoria.GASTO).build(),          // 1
                Categoria.builder()
                        .nome("Lazer")
                        .icone("sports-esports")
                        .cor("#FDCB6E")
                        .tipo(TipoCategoria.GASTO).build(),          // 2
                Categoria.builder()
                        .nome("Saúde")
                        .icone("local-hospital")
                        .cor("#A8D8A8")
                        .tipo(TipoCategoria.GASTO).build(),          // 3
                Categoria.builder()
                        .nome("Moradia")
                        .icone("home")
                        .cor("#FD79A8")
                        .tipo(TipoCategoria.GASTO).build(),          // 4
                Categoria.builder()
                        .nome("Salário")
                        .icone("attach-money")
                        .cor("#FFD700")
                        .tipo(TipoCategoria.RECEITA).build(),        // 5
                Categoria.builder()
                        .nome("Investimento")
                        .icone("trending-up")
                        .cor("#00C853")
                        .tipo(TipoCategoria.RECEITA).build(),        // 6
                Categoria.builder()
                        .nome("Mercado")
                        .icone("local-grocery-store")
                        .cor("#FF8C00")
                        .tipo(TipoCategoria.GASTO).build(),          // 7
                Categoria.builder()
                        .nome("Renda Extra")
                        .icone("work")
                        .cor("#A855F7")
                        .tipo(TipoCategoria.RECEITA).build(),        // 8
                Categoria.builder()
                        .nome("Educação")
                        .icone("school")
                        .cor("#6C63FF")
                        .tipo(TipoCategoria.GASTO).build(),          // 9
                Categoria.builder()
                        .nome("Telecom")
                        .icone("smartphone")
                        .cor("#00BCD4")
                        .tipo(TipoCategoria.GASTO).build()           // 10
        );

        List<UUID> categoriaIds = new ArrayList<>();
        for (Categoria categoria : categoriasParaSalvar) {
            Categoria saved = categoriaRepository.save(categoria);
            categoriaIds.add(saved.getId());
        }
        log.info("Categorias cadastradas com sucesso!");

        // ─────────────────────────────────────────────────────────────────────
        // Objetivos — 5 cenários de KPI para validação do dashboard
        // ─────────────────────────────────────────────────────────────────────

        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate fimMes    = inicioMes.plusMonths(1).minusDays(1);

        List<Objetivo> objetivos = List.of(
                // KPI 0 — limite de gasto em alimentação
                Objetivo.builder()
                        .nome("Limitar delivery e refeições")
                        .descricao("Meta R$ 600 — realizado R$ 300 (~50%, CUIDADO, no ritmo)")
                        .valor(new BigDecimal("600.00"))
                        .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                        .prioridade(PrioridadeObjetivo.ALTA)
                        .dataInicio(inicioMes)
                        .dataFim(fimMes)
                        .fkUsuario(usuarioId)
                        .fkCategoria(categoriaIds.get(0))   // Alimentação
                        .build(),
                // KPI 1 — limite de gasto em transporte
                Objetivo.builder()
                        .nome("Limitar transporte")
                        .descricao("Meta R$ 300 — realizado R$ 240 (~80%, CUIDADO, no ritmo)")
                        .valor(new BigDecimal("300.00"))
                        .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                        .prioridade(PrioridadeObjetivo.MEDIA)
                        .dataInicio(inicioMes)
                        .dataFim(fimMes)
                        .fkUsuario(usuarioId)
                        .fkCategoria(categoriaIds.get(1))   // Transporte
                        .build(),
                // KPI 2 — limite de lazer (estourado, maior alerta)
                Objetivo.builder()
                        .nome("Teto de lazer no mês")
                        .descricao("Meta R$ 500 — realizado R$ 690 (~138%, ACIMA_DO_COMBINADO, maior alerta)")
                        .valor(new BigDecimal("500.00"))
                        .tipoObjetivo(TipoObjetivo.LIMITE_GASTO)
                        .prioridade(PrioridadeObjetivo.BAIXA)
                        .dataInicio(inicioMes)
                        .dataFim(fimMes)
                        .fkUsuario(usuarioId)
                        .fkCategoria(categoriaIds.get(2))   // Lazer
                        .build(),
                // KPI 3 — meta de renda extra com freelas
                Objetivo.builder()
                        .nome("Renda extra com freelas")
                        .descricao("Meta R$ 400 — realizado R$ 380 (~95%, FALTA_POUCO, no ritmo)")
                        .valor(new BigDecimal("400.00"))
                        .tipoObjetivo(TipoObjetivo.AUMENTO_RECEITA)
                        .prioridade(PrioridadeObjetivo.ALTA)
                        .dataInicio(inicioMes)
                        .dataFim(fimMes)
                        .fkUsuario(usuarioId)
                        .fkCategoria(categoriaIds.get(8))   // Renda Extra
                        .build(),
                // KPI 4 — meta de receita com investimentos
                Objetivo.builder()
                        .nome("Receita com investimentos")
                        .descricao("Meta R$ 200 — realizado R$ 50 (~25%, NO_CAMINHO, fora do ritmo)")
                        .valor(new BigDecimal("200.00"))
                        .tipoObjetivo(TipoObjetivo.AUMENTO_RECEITA)
                        .dataInicio(inicioMes)
                        .dataFim(fimMes)
                        .fkUsuario(usuarioId)
                        .fkCategoria(categoriaIds.get(6))   // Investimento
                        .build()
        );

        for (Objetivo objetivo : objetivos) {
            objetivoRepository.save(objetivo);
        }
        log.info("Objetivos cadastrados com sucesso!");

        // ─────────────────────────────────────────────────────────────────────
        // Transações — mês atual, todas com datas nos dias 1–4 (passado)
        //
        // Saldos esperados:
        //   Nubank:            R$ 4.430 receitas − R$ 3.250 gastos = R$ 1.180,00
        //   Vale Refeição:     R$ 1.200 recarga  − R$   250 gastos = R$   950,00
        //   Vale Alimentação:  R$   800 recarga   − R$   250 gastos = R$   550,00
        //   Total consolidado:                                         R$ 2.680,00
        // ─────────────────────────────────────────────────────────────────────

        LocalDateTime agora = LocalDateTime.now();

        LocalDateTime dia01 = inicioMes.atTime(8, 0);
        LocalDateTime dia02 = inicioMes.plusDays(1).atTime(9, 0);
        LocalDateTime dia03 = inicioMes.plusDays(2).atTime(10, 0);
        LocalDateTime dia04 = inicioMes.plusDays(3).atTime(11, 0);

        // fimRecorrencia: 12 meses à frente
        Date fimRecorrencia = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(fimRecorrencia);
        cal.add(Calendar.MONTH, 12);
        fimRecorrencia = cal.getTime();

        List<Transacao> transacoes = new ArrayList<>(List.of(

                // ══════════════════════════════════════════
                // NUBANK — Receitas
                // ══════════════════════════════════════════

                // Salário mensal — Nubank, categoria Salário ✓
                Transacao.builder()
                        .valor(4000.00)
                        .tipo(TipoTransacao.RECEITA)
                        .descricao("Salário")
                        .dataTransacao(dia02)
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))   // Nubank ✓
                        .fkCategoria(categoriaIds.get(5))        // Salário ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // Freela design — Nubank ✓, Renda Extra ✓ (KPI 3)
                Transacao.builder()
                        .valor(380.00)
                        .tipo(TipoTransacao.RECEITA)
                        .descricao("Freela design")
                        .dataTransacao(dia03)
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))   // Nubank ✓
                        .fkCategoria(categoriaIds.get(8))        // Renda Extra ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // Dividendos CDB — Nubank ✓, Investimento ✓ (KPI 4)
                Transacao.builder()
                        .valor(50.00)
                        .tipo(TipoTransacao.RECEITA)
                        .descricao("Dividendos CDB")
                        .dataTransacao(dia03)
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))   // Nubank ✓
                        .fkCategoria(categoriaIds.get(6))        // Investimento ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // ══════════════════════════════════════════
                // NUBANK — Gastos fixos mensais
                // ══════════════════════════════════════════

                // Aluguel — Moradia ✓
                Transacao.builder()
                        .valor(1200.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Aluguel")
                        .dataTransacao(dia02)
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(4))        // Moradia ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // Conta de Luz — Moradia ✓ (gasto da casa)
                Transacao.builder()
                        .valor(120.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Conta de Luz")
                        .dataTransacao(dia02)
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(4))        // Moradia ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // Academia Smart Fit — Saúde ✓ (não é Educação!)
                Transacao.builder()
                        .valor(80.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Academia Smart Fit")
                        .dataTransacao(dia02)
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(3))        // Saúde ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // Netflix e Spotify — Lazer ✓ (entretenimento, não Educação!)
                Transacao.builder()
                        .valor(70.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Netflix e Spotify")
                        .dataTransacao(dia02)
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(2))        // Lazer ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // Plano celular — Telecom ✓ (não é Educação!)
                Transacao.builder()
                        .valor(80.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Plano celular")
                        .dataTransacao(dia01)
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(10))       // Telecom ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // Internet em casa — Telecom ✓
                Transacao.builder()
                        .valor(100.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Internet em casa")
                        .dataTransacao(dia01)
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(10))       // Telecom ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // ══════════════════════════════════════════
                // NUBANK — KPI objetivos (mês atual)
                // ══════════════════════════════════════════

                // Delivery iFood — KPI Alimentação (100+200 = 300/600 = 50%)
                Transacao.builder()
                        .valor(100.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Delivery iFood")
                        .dataTransacao(dia03)
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(0))        // Alimentação ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // Combustível e apps — KPI Transporte (240/300 = 80%)
                Transacao.builder()
                        .valor(240.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Combustível e apps")
                        .dataTransacao(dia03)
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(1))        // Transporte ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // Compras lazer — KPI Lazer (620+70 = 690/500 = 138%)
                Transacao.builder()
                        .valor(620.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Compras lazer")
                        .dataTransacao(dia03)
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(2))        // Lazer ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // ══════════════════════════════════════════
                // NUBANK — Gastos variáveis
                // ══════════════════════════════════════════

                // Supermercado — Mercado ✓
                Transacao.builder()
                        .valor(320.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Supermercado")
                        .dataTransacao(dia04)
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(7))        // Mercado ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // Jantar restaurante — Alimentação ✓
                Transacao.builder()
                        .valor(200.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Jantar restaurante")
                        .dataTransacao(dia03)
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(0))        // Alimentação ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // Farmácia — Saúde ✓ (não é Mercado!)
                Transacao.builder()
                        .valor(120.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Farmácia")
                        .dataTransacao(dia04)
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(3))        // Saúde ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // Curso online Udemy — Educação ✓
                Transacao.builder()
                        .valor(120.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Curso online Udemy")
                        .dataTransacao(dia03)
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(9))        // Educação ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // ══════════════════════════════════════════
                // VALE REFEIÇÃO — R$ 1.200/mês (almoço/refeições)
                // ══════════════════════════════════════════

                // Recarga mensal do empregador — Vale Refeição, Salário ✓
                Transacao.builder()
                        .valor(1200.00)
                        .tipo(TipoTransacao.RECEITA)
                        .descricao("Recarga Vale Refeição")
                        .dataTransacao(dia01)
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(1))   // Vale Refeição ✓
                        .fkCategoria(categoriaIds.get(5))        // Salário (benefício do empregador) ✓
                        .criadoEm(agora)
                        .atualizadoEm(agora)
                        .build(),

                // Almoço no restaurante da empresa — Alimentação ✓
                Transacao.builder()
                        .valor(55.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Almoço na empresa")
                        .dataTransacao(dia03)
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(1))   // Vale Refeição ✓
                        .fkCategoria(categoriaIds.get(0))        // Alimentação ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // Lanche / café — Alimentação ✓
                Transacao.builder()
                        .valor(50.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Café e lanche")
                        .dataTransacao(dia04)
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(1))   // Vale Refeição ✓
                        .fkCategoria(categoriaIds.get(0))        // Alimentação ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // ══════════════════════════════════════════
                // VALE ALIMENTAÇÃO — R$ 800/mês (supermercado)
                // ══════════════════════════════════════════

                // Recarga mensal do empregador — Vale Alimentação, Salário ✓
                Transacao.builder()
                        .valor(800.00)
                        .tipo(TipoTransacao.RECEITA)
                        .descricao("Recarga Vale Alimentação")
                        .dataTransacao(dia01)
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(2))   // Vale Alimentação ✓
                        .fkCategoria(categoriaIds.get(5))        // Salário (benefício do empregador) ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // Supermercado do bairro — Mercado ✓
                Transacao.builder()
                        .valor(150.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Supermercado do bairro")
                        .dataTransacao(dia03)
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(2))   // Vale Alimentação ✓
                        .fkCategoria(categoriaIds.get(7))        // Mercado ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build(),

                // Hortifrúti / feira — Mercado ✓
                Transacao.builder()
                        .valor(100.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Hortifrúti e feira")
                        .dataTransacao(dia04)
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(2))   // Vale Alimentação ✓
                        .fkCategoria(categoriaIds.get(7))        // Mercado ✓
                        .criadoEm(agora).atualizadoEm(agora)
                        .build()
        ));

        for (Transacao transacao : transacoes) {
            transacaoRepository.save(transacao);
        }
        log.info("Transacoes cadastradas com sucesso!");

        LocalDateTime dataInicioRecalculo = inicioMes.atStartOfDay();
        for (UUID instId : instituicaoIds) {
            try {
                saldoDiarioCalculadorService.recalcular(instId, dataInicioRecalculo.toLocalDate());
                log.info("Saldo diário recalculado para instituição: {}", instId);
            } catch (Exception e) {
                log.warn("Falha ao recalcular saldo diário para instituição {}: {}", instId, e.getMessage());
            }
        }

        log.info("""
                Mock — saldos esperados (mês atual):
                  Nubank:            R$ 4.430 receitas − R$ 3.250 gastos = R$ 1.180,00
                  Vale Refeição:     R$ 1.200 recarga  − R$   250 gastos = R$   950,00
                  Vale Alimentação:  R$   800 recarga  − R$   250 gastos = R$   550,00
                  Total consolidado:                                         R$ 2.680,00
                  
                Mock KPI objetivos — valores esperados (mês atual):
                  Alimentação:   R$ 300/600  = 50%%  → CUIDADO
                  Transporte:    R$ 240/300  = 80%%  → CUIDADO
                  Lazer:         R$ 690/500  = 138%% → ACIMA_DO_COMBINADO (maior alerta)
                  Renda Extra:   R$ 380/400  = 95%%  → FALTA_POUCO
                  Investimento:  R$  50/200  = 25%%  → NO_CAMINHO
                """);
    }
}

package br.com.contadin.infrastructure.initializer;

import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.application.port.out.ObjetivoRepository;
import br.com.contadin.application.port.out.TransacaoRepository;
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
                        .tipo(TipoCategoria.GASTO)
                        .fkUsuario(usuarioId).build(),
                Categoria.builder()
                        .nome("Transporte")
                        .icone("directions-car")
                        .cor("#4ECDC4")
                        .tipo(TipoCategoria.GASTO)
                        .fkUsuario(usuarioId).build(),
                Categoria.builder()
                        .nome("Lazer")
                        .icone("sports-esports")
                        .cor("#FDCB6E")
                        .tipo(TipoCategoria.GASTO)
                        .fkUsuario(usuarioId).build(),
                Categoria.builder()
                        .nome("Outros")
                        .icone("category")
                        .cor("#A29BFE")
                        .tipo(TipoCategoria.RECEITA)
                        .fkUsuario(usuarioId).build(),
                Categoria.builder()
                        .nome("Aluguel")
                        .icone("home")
                        .cor("#FD79A8")
                        .tipo(TipoCategoria.RECEITA)
                        .fkUsuario(usuarioId).build()
        );

        List<UUID> categoriaIds = new ArrayList<>();
        for (Categoria categoria : categoriasParaSalvar) {
            Categoria saved = categoriaRepository.save(categoria);
            categoriaIds.add(saved.getId());
        }
        log.info("Categorias cadastradas com sucesso!");

        // Objetivos (5 cenários para validar KPIs — ver comentário no fim do método)
        // índices: 0=Alimentação, 1=Transporte, 2=Lazer, 3=Outros(receita), 4=Aluguel(receita)

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
                        .fkCategoria(categoriaIds.get(3))
                        .build(),
                Objetivo.builder()
                        .nome("Receita com investimentos")
                        .descricao("Meta R$ 200 — realizado R$ 50 (~25%, NO_CAMINHO, fora do ritmo)")
                        .valor(new BigDecimal("200.00"))
                        .tipoObjetivo(TipoObjetivo.AUMENTO_RECEITA)
                        .dataInicio(inicioMes)
                        .dataFim(fimMes)
                        .fkUsuario(usuarioId)
                        .fkCategoria(categoriaIds.get(4))
                        .build()
        );

        for (Objetivo objetivo : objetivos) {
            objetivoRepository.save(objetivo);
        }
        log.info("Objetivos cadastrados com sucesso!");

        // Transacoes (valores alinhados aos objetivos acima)

        Date fimRecorrencia = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(fimRecorrencia);
        cal.add(Calendar.MONTH, 12);
        fimRecorrencia = cal.getTime();

        LocalDateTime dataNoMes = inicioMes.plusDays(10).atTime(12, 0);
        LocalDateTime agora = LocalDateTime.now();

        List<Transacao> transacoes = List.of(
                Transacao.builder()
                        .valor(100.00)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Delivery — mock KPI delivery")
                        .dataTransacao(dataNoMes)
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
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
                        .fkInstituicao(instituicaoIds.get(1))
                        .fkCategoria(categoriaIds.get(3))
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
                        .fkInstituicao(instituicaoIds.get(1))
                        .fkCategoria(categoriaIds.get(4))
                        .criadoEm(agora)
                        .atualizadoEm(agora)
                        .build()
        );

        for (Transacao transacao : transacoes) {
            transacaoRepository.save(transacao);
        }
        log.info("Transacoes cadastradas com sucesso!");

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

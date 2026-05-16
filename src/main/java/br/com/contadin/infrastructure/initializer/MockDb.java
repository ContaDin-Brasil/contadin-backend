package br.com.contadin.infrastructure.initializer;

import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.application.port.out.ObjetivoRepository;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.application.service.SaldoDiarioCalculadorService;
import br.com.contadin.domain.enums.Recorrencia;
import br.com.contadin.domain.enums.TipoCategoria;
import br.com.contadin.domain.enums.TipoInstituicao;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
                        .tipo(TipoCategoria.GASTO)
                        .fkUsuario(usuarioId).build(),
                Categoria.builder()
                        .nome("Transporte")
                        .icone("directions-car")
                        .cor("#4ECDC4")
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

        // Objetivo

        Date dataFim = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dataFim);
        c.add(Calendar.MONTH, 3);
        dataFim = c.getTime();

        List<Objetivo> objetivos = List.of(
                Objetivo.builder()
                        .nome("Objetivo Supermercado")
                        .valor(new BigDecimal("1500.00"))
                        .dataFimObjetivo(dataFim)
                        .criadoEm(LocalDateTime.now())
                        .fkUsuario(usuarioId)
                        .fkCategoria(categoriaIds.get(0))
                        .build(),
                Objetivo.builder()
                        .nome("Objetivo Transporte")
                        .valor(new BigDecimal("400.00"))
                        .dataFimObjetivo(dataFim)
                        .criadoEm(LocalDateTime.now())
                        .fkUsuario(usuarioId)
                        .fkCategoria(categoriaIds.get(1))
                        .build()
        );

        for (Objetivo objetivo : objetivos) {
            objetivoRepository.save(objetivo);
        }
        log.info("Objetivos cadastradas com sucesso!");

        // Transacoes

        Date fimRecorrencia = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(fimRecorrencia);
        cal.add(Calendar.MONTH, 12);
        fimRecorrencia = cal.getTime();

        LocalDateTime tresMesesAtras = LocalDateTime.now().minusMonths(3).withDayOfMonth(1);

        List<Transacao> transacoes = List.of(
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
                        .fkCategoria(categoriaIds.get(2))
                        .criadoEm(LocalDateTime.now())
                        .atualizadoEm(LocalDateTime.now())
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
                        .fkCategoria(categoriaIds.get(3))
                        .criadoEm(LocalDateTime.now())
                        .atualizadoEm(LocalDateTime.now())
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
                        .criadoEm(LocalDateTime.now())
                        .atualizadoEm(LocalDateTime.now())
                        .build(),
                // Nubank - Transporte pontual (hoje)
                Transacao.builder()
                        .valor(89.90)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Transporte")
                        .dataTransacao(LocalDateTime.now())
                        .parcelado(false)
                        .recorrencia(null)
                        .ativo(true)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(1))
                        .criadoEm(LocalDateTime.now())
                        .atualizadoEm(LocalDateTime.now())
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
                        .criadoEm(LocalDateTime.now())
                        .atualizadoEm(LocalDateTime.now())
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
                        .fkCategoria(categoriaIds.get(2))
                        .criadoEm(LocalDateTime.now())
                        .atualizadoEm(LocalDateTime.now())
                        .build()
        );

        for (Transacao transacao : transacoes) {
            transacaoRepository.save(transacao);
        }
        log.info("Transacoes cadastradas com sucesso!");

        // Recalcula saldo diário para cada instituição a partir da transação mais antiga
        LocalDateTime dataInicioRecalculo = tresMesesAtras;
        for (UUID instId : instituicaoIds) {
            try {
                saldoDiarioCalculadorService.recalcular(instId, dataInicioRecalculo.toLocalDate());
                log.info("Saldo diário recalculado para instituição: {}", instId);
            } catch (Exception e) {
                log.warn("Falha ao recalcular saldo diário para instituição {}: {}", instId, e.getMessage());
            }
        }
    }
}

package br.com.contadin.infrastructure.initializer;

import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.application.port.out.ObjetivoRepository;
import br.com.contadin.application.port.out.TransacaoRepository;
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

        List<Transacao> transacoes = List.of(
                Transacao.builder()
                        .valor(150.50)
                        .tipo(TipoTransacao.GASTO)
                        .descricao("Supermercado")
                        .dataTransacao(LocalDateTime.now())
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .fkInstituicao(instituicaoIds.get(0))
                        .fkCategoria(categoriaIds.get(0))
                        .criadoEm(LocalDateTime.now())
                        .atualizadoEm(LocalDateTime.now())
                        .build(),
                Transacao.builder()
                        .valor(3500.00)
                        .tipo(TipoTransacao.RECEITA)
                        .descricao("Salário")
                        .dataTransacao(LocalDateTime.now())
                        .parcelado(false)
                        .recorrencia(Recorrencia.MENSAL)
                        .fimRecorrencia(fimRecorrencia)
                        .fkInstituicao(instituicaoIds.get(1))
                        .fkCategoria(categoriaIds.get(1))
                        .criadoEm(LocalDateTime.now())
                        .atualizadoEm(LocalDateTime.now())
                        .build()
        );

        for (Transacao transacao : transacoes) {
            transacaoRepository.save(transacao);
        }
        log.info("Transacoes cadastradas com sucesso!");
    }
}

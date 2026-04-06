package br.com.contadin.infrastructure.initializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.application.port.out.MetaGastoRepository;
import br.com.contadin.application.port.out.TransacaoRepository;
// import br.com.contadin.application.port.out.UsuarioRepository;

import br.com.contadin.domain.enums.Recorrencia;
import br.com.contadin.domain.enums.TipoInstituicao;
import br.com.contadin.domain.enums.TipoTransacao;
import br.com.contadin.domain.model.Categoria;
import br.com.contadin.domain.model.Instituicao;
import br.com.contadin.domain.model.MetaGasto;
import br.com.contadin.domain.model.Transacao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(2)
public class MockDb implements ApplicationRunner{

    // Repositories
    // private final UsuarioRepository usuarioRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MetaGastoRepository metaGastoRepository;
    private final TransacaoRepository transacaoRepository;

    // Environment
    @Value("${app.set.mockdata:false}")
    private Boolean setMockData;

    @Override
    public void run(ApplicationArguments args) {

        if (!setMockData) {
            log.info("Aplicação iniciando sem dados mocados");
            return;
        }

        if (!instituicaoRepository.findAtivasByUsuario(1).isEmpty()) {
            log.info("Dados mock já existem; pulando inicialização.");
            return;
        }

        // ===========
        // -- Mocks -- 
        // ===========

        // Instituicoes

        List<Instituicao> instituicoes = List.of(
            Instituicao.builder()
                .nome("Nubank")
                .icone("nubank")
                .cor("#820AD1")
                .tipo(TipoInstituicao.BANCO)
                .fkUsuario(1)
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build(),
            Instituicao.builder()
                .nome("Vale Alimentação")
                .icone("vale")
                .cor("#00A86B")
                .tipo(TipoInstituicao.VALE)
                .fkUsuario(1)
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build()
        );

        for (Instituicao instituicao : instituicoes) {
            instituicaoRepository.save(instituicao);
        }
        log.info("Instituicoes cadastras com sucesso!");

        // Categorias

        List<Categoria> categorias = List.of(
            Categoria.builder().nome("Alimentação").fkUsuario(1).build(),
            Categoria.builder().nome("Transporte").fkUsuario(1).build(),
            Categoria.builder().nome("Lazer").fkUsuario(1).build(),
            Categoria.builder().nome("Saúde").fkUsuario(1).build()
        );

        for (Categoria categoria : categorias) {
            categoriaRepository.save(categoria);
        }
        log.info("Categorias cadastras com sucesso!");

        // Meta Gasto

        Date dataFim = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dataFim);
        c.add(Calendar.MONTH, 3);
        dataFim = c.getTime();

        List<MetaGasto> metasGasto = List.of(
            MetaGasto.builder()
                .nome("Meta Supermercado")
                .valor(new BigDecimal("1500.00"))
                .dataFimMeta(dataFim)
                .criadoEm(LocalDateTime.now())
                .fkUsuario(1)
                .fkCategoria(1)
                .build(),
            MetaGasto.builder()
                .nome("Meta Transporte")
                .valor(new BigDecimal("400.00"))
                .dataFimMeta(dataFim)
                .criadoEm(LocalDateTime.now())
                .fkUsuario(1)
                .fkCategoria(2)
                .build()
        );

        for (MetaGasto metaGasto : metasGasto) {
            metaGastoRepository.save(metaGasto);
        }
        log.info("Metas de gasto cadastras com sucesso!");

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
                .fkInstituicao(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .fkCategoria(UUID.fromString("00000000-0000-0000-0000-000000000011"))
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
                .fkInstituicao(UUID.fromString("00000000-0000-0000-0000-000000000002"))
                .fkCategoria(UUID.fromString("00000000-0000-0000-0000-000000000012"))
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build()
        );

        for (Transacao transacao : transacoes) {
            transacaoRepository.save(transacao);
        }
        log.info("Transacoes cadastras com sucesso!");
    }
}
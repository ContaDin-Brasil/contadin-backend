package br.com.contadin.application.usecase.transacao;

import br.com.contadin.application.port.in.transacao.CriarMultiplasTransacoesInputPort;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.exception.transacao.TransacaoInvalidaException;
import br.com.contadin.domain.model.Transacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CriarMultiplasTransacoesUseCase implements CriarMultiplasTransacoesInputPort {

    private final TransacaoRepository transacaoRepository;

    @Override
    @Transactional
    public List<Transacao> execute(List<Transacao> transacoes) {
        if (transacoes == null || transacoes.isEmpty()) {
            throw new TransacaoInvalidaException("A lista de transações não pode ser vazia.");
        }

        List<Transacao> paraSalvar = new ArrayList<>();

        for (int i = 0; i < transacoes.size(); i++) {
            Transacao transacao = transacoes.get(i);
            int posicao = i + 1;
            validarTransacao(transacao, posicao);
            paraSalvar.add(Transacao.builder()
                    .id(transacao.getId())
                    .valor(transacao.getValor())
                    .tipo(transacao.getTipo())
                    .descricao(transacao.getDescricao())
                    .dataTransacao(transacao.getDataTransacao())
                    .parcelado(transacao.getParcelado())
                    .recorrencia(transacao.getRecorrencia())
                    .fimRecorrencia(transacao.getFimRecorrencia())
                    .ativo(transacao.getAtivo() != null ? transacao.getAtivo() : true)
                    .fkInstituicao(transacao.getFkInstituicao())
                    .fkCategoria(transacao.getFkCategoria())
                    .build());
        }

        return transacaoRepository.saveAll(paraSalvar);
    }

    private void validarTransacao(Transacao transacao, int posicao) {
        if (transacao.getValor() == null || transacao.getValor() <= 0) {
            throw new TransacaoInvalidaException(
                    "Transação " + posicao + ": o valor deve ser maior que zero.");
        }
        if (transacao.getTipo() == null) {
            throw new TransacaoInvalidaException(
                    "Transação " + posicao + ": o tipo é obrigatório.");
        }
        if (transacao.getDataTransacao() == null) {
            throw new TransacaoInvalidaException(
                    "Transação " + posicao + ": a data é obrigatória.");
        }
    }
}

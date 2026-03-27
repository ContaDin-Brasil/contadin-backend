package br.com.contadin.application.usecase.transacao;

import br.com.contadin.application.port.in.transacao.CriarTransacaoInputPort;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.exception.transacao.TransacaoInvalidaException;
import br.com.contadin.domain.model.Transacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CriarTransacaoUseCase implements CriarTransacaoInputPort {

    private final TransacaoRepository transacaoRepository;


    @Override
    public Transacao execute(Transacao transacao) {
        validarTransacao(transacao);
        Transacao transacaoParaSalvar = Transacao.builder()
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
                .criadoEm(transacao.getCriadoEm())
                .atualizadoEm(transacao.getAtualizadoEm())
                .build();

        return transacaoRepository.save(transacaoParaSalvar);
    }

    private void validarTransacao(Transacao transacao) {
        if (transacao.getValor() == null || transacao.getValor() <= 0) {
            throw new TransacaoInvalidaException("O valor da transação deve ser maior que zero.");
        }
        if (transacao.getTipo() == null) {
            throw new TransacaoInvalidaException("O tipo da transação é obrigatório.");
        }
        if (transacao.getDataTransacao() == null) {
            throw new TransacaoInvalidaException("A data da transação é obrigatória.");
        }
    }

}

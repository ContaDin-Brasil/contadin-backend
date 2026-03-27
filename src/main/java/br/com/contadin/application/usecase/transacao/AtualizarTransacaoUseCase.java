package br.com.contadin.application.usecase.transacao;

import br.com.contadin.application.port.in.transacao.AtualizarTransacaoInputPort;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.exception.transacao.TransacaoInvalidaException;
import br.com.contadin.domain.exception.transacao.TransacaoNaoEncontradaException;
import br.com.contadin.domain.model.Transacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AtualizarTransacaoUseCase implements AtualizarTransacaoInputPort {

    private final TransacaoRepository transacaoRepository;

    @Override
    public Transacao execute(Integer id, Transacao transacao) {
        if (id == null) {
            throw new TransacaoInvalidaException("ID da transação é obrigatório para atualização.");
        }

        Transacao existente = transacaoRepository.findById(id)
                .orElseThrow(() -> new TransacaoNaoEncontradaException("Transação não encontrada."));

        Transacao toSave = Transacao.builder()
                .id(existente.getId())
                .valor(transacao.getValor() != null ? transacao.getValor() : existente.getValor())
                .tipo(transacao.getTipo() != null ? transacao.getTipo() : existente.getTipo())
                .descricao(transacao.getDescricao() != null ? transacao.getDescricao() : existente.getDescricao())
                .dataTransacao(transacao.getDataTransacao() != null ? transacao.getDataTransacao() : existente.getDataTransacao())
                .parcelado(transacao.getParcelado() != null ? transacao.getParcelado() : existente.getParcelado())
                .recorrencia(transacao.getRecorrencia() != null ? transacao.getRecorrencia() : existente.getRecorrencia())
                .fimRecorrencia(transacao.getFimRecorrencia() != null ? transacao.getFimRecorrencia() : existente.getFimRecorrencia())
                .ativo(transacao.getAtivo() != null ? transacao.getAtivo() : existente.getAtivo())
                .fkInstituicao(transacao.getFkInstituicao() != null ? transacao.getFkInstituicao() : existente.getFkInstituicao())
                .fkCategoria(transacao.getFkCategoria() != null ? transacao.getFkCategoria() : existente.getFkCategoria())
                .criadoEm(existente.getCriadoEm())
                .atualizadoEm(LocalDateTime.now())
                .build();

            validarTransacao(toSave);

        return transacaoRepository.save(toSave);
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

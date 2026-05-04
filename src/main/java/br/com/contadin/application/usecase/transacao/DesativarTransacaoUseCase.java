package br.com.contadin.application.usecase.transacao;

import br.com.contadin.application.port.in.transacao.DesativarTransacaoInputPort;
import br.com.contadin.application.port.out.TransacaoRepository;
import br.com.contadin.domain.exception.transacao.TransacaoInvalidaException;
import br.com.contadin.domain.exception.transacao.TransacaoNaoEncontradaException;
import br.com.contadin.domain.model.Transacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DesativarTransacaoUseCase implements DesativarTransacaoInputPort {

    private final TransacaoRepository transacaoRepository;

    @Override
    public void execute(UUID id) {
        if (id == null) {
            throw new TransacaoInvalidaException("ID da transação é obrigatório para desativação.");
        }

        Transacao existente = transacaoRepository.findById(id)
                .orElseThrow(() -> new TransacaoNaoEncontradaException("Transação não encontrada."));

        Integer qtdParcelas = Boolean.TRUE.equals(existente.getParcelado()) ? existente.getQtdParcelas() : null;

        Transacao toSave = Transacao.builder()
                .id(existente.getId())
                .valor(existente.getValor())
                .tipo(existente.getTipo())
                .descricao(existente.getDescricao())
                .dataTransacao(existente.getDataTransacao())
                .parcelado(existente.getParcelado())
                .qtdParcelas(qtdParcelas)
                .recorrencia(existente.getRecorrencia())
                .fimRecorrencia(existente.getFimRecorrencia())
                .ativo(false)
                .fkInstituicao(existente.getFkInstituicao())
                .fkCategoria(existente.getFkCategoria())
                .criadoEm(existente.getCriadoEm())
                .atualizadoEm(LocalDateTime.now())
                .build();

        transacaoRepository.save(toSave);
    }
}
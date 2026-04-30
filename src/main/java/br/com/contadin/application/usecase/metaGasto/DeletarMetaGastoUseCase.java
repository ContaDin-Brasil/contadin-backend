package br.com.contadin.application.usecase.metaGasto;

import br.com.contadin.application.port.in.metaGasto.DeletarMetaGastoInputPort;
import br.com.contadin.application.port.out.MetaGastoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletarMetaGastoUseCase implements DeletarMetaGastoInputPort {

    private final MetaGastoRepository metaGastoRepository;

    @Override
    public void execute(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID da meta de gasto é obrigatório para exclusão");
        }

        metaGastoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Meta não encontrada"));

        metaGastoRepository.deleteById(id);
    }
}

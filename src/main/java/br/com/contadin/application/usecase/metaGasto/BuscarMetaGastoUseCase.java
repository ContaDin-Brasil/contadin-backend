package br.com.contadin.application.usecase.metaGasto;

import br.com.contadin.application.port.in.metaGasto.BuscarMetaGastoInputPort;
import br.com.contadin.application.port.out.MetaGastoRepository;
import br.com.contadin.domain.model.MetaGasto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BuscarMetaGastoUseCase implements BuscarMetaGastoInputPort {

    private final MetaGastoRepository metaGastoRepository;

    @Override
    public List<MetaGasto> execute(Integer fkUsuario) {
        if (fkUsuario == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório para busca");
        }

        return metaGastoRepository.findByUsuario(fkUsuario);
    }

    @Override
    public MetaGasto executeBuscarPorId(Integer metaGastoId) {
        if (metaGastoId == null) {
            throw new IllegalArgumentException("ID da meta de gasto é obrigatório para busca");
        }

        return metaGastoRepository.findById(metaGastoId)
                .orElseThrow(() -> new IllegalArgumentException("Meta de gasto não encontrada"));

    }

    @Override
    public List<MetaGasto> executeBuscarPorNome(String nome, Integer fkUsuario) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da meta de gasto é obrigatório para busca");
        }
        if (fkUsuario == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório para busca");
        }

        return metaGastoRepository.findByNomeAndUsuario(nome, fkUsuario);
    }
}

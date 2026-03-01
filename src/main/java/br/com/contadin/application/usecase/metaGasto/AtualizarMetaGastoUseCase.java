package br.com.contadin.application.usecase.metaGasto;

import br.com.contadin.application.port.in.metaGasto.AtualizarMetaGastoInputPort;
import br.com.contadin.application.port.out.MetaGastoRepository;
import br.com.contadin.domain.model.MetaGasto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtualizarMetaGastoUseCase implements AtualizarMetaGastoInputPort {

        private final MetaGastoRepository metaGastoRepository;

        @Override
        public MetaGasto execute(Integer id, MetaGasto metaGasto) {
            if (id == null) {
                throw new IllegalArgumentException("ID da meta é obrigatório para atualização");
            }

            MetaGasto existente = metaGastoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Meta não encontrada"));


            MetaGasto.MetaGastoBuilder builder = MetaGasto.builder()
                    .id(existente.getId())
                    .nome(metaGasto.getNome() != null ? metaGasto.getNome() : existente.getNome())
                    .valor(metaGasto.getValor() != null ? metaGasto.getValor() : existente.getValor())
                    .fkUsuario(existente.getFkUsuario())
                    .fkCategoria(metaGasto.getFkCategoria() != null ? metaGasto.getFkCategoria() : existente.getFkCategoria())
                    .dataFimMeta(metaGasto.getDataFimMeta() != null ? metaGasto.getDataFimMeta() : existente.getDataFimMeta())
                    .criadoEm(existente.getCriadoEm());

            MetaGasto toSave = builder.build();
            return metaGastoRepository.save(toSave);

        }
}

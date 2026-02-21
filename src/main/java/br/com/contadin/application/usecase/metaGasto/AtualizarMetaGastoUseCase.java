package br.com.contadin.application.usecase.metaGasto;

import br.com.contadin.application.port.in.metaGasto.AtualizarMetaGastoInputPort;
import br.com.contadin.application.port.out.MetaGastoRepository;
import br.com.contadin.domain.model.MetaGasto;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class AtualizarMetaGastoUseCase implements AtualizarMetaGastoInputPort {

        private final MetaGastoRepository metaGastoRepository;

        @Override
        public MetaGasto execute(MetaGasto metaGasto) {
            if (metaGasto == null || metaGasto.getId() == null) {
                throw new IllegalArgumentException("ID da meta é obrigatório para atualização");
            }

            MetaGasto existente = metaGastoRepository.findById(metaGasto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Meta não encontrada"));


            MetaGasto.MetaGastoBuilder builder = MetaGasto.builder()
                    .id(existente.getId())
                    .nome(metaGasto.getNome() != null ? metaGasto.getNome() : existente.getNome())
                    .valor(metaGasto.getValor() != null ? metaGasto.getValor() : existente.getValor())
                    .fkUsuario(existente.getFkUsuario())
                    .dataFimMeta(metaGasto.getDataFimMeta() != null ? metaGasto.getDataFimMeta() : existente.getDataFimMeta())
                    .criadoEm(existente.getCriadoEm());

            MetaGasto toSave = builder.build();
            return metaGastoRepository.save(toSave);

        }
}

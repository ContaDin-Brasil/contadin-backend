package br.com.contadin.application.usecase.metaGasto;

import br.com.contadin.application.port.in.metaGasto.CriarMetaGastoInputPort;
import br.com.contadin.application.port.out.MetaGastoRepository;
import br.com.contadin.domain.model.MetaGasto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CriarMetaGastoUseCase implements CriarMetaGastoInputPort {
    private final MetaGastoRepository metaGastoRepository;

    @Override
    public MetaGasto execute(MetaGasto metaGasto) {
        validarCampos(metaGasto);

        LocalDateTime now = LocalDateTime.now();

        MetaGasto.MetaGastoBuilder builder = MetaGasto.builder()
                .nome(metaGasto.getNome())
                .valor(metaGasto.getValor())
                .dataFimMeta(metaGasto.getDataFimMeta())
                .fkUsuario(metaGasto.getFkUsuario())
                .fkCategoria(metaGasto.getFkCategoria())
                .atualizadoEm(now)
                .criadoEm(now);


        MetaGasto toSave = builder.build();
        return metaGastoRepository.save(toSave);
    }

    public void validarCampos(MetaGasto metaGasto) {
        if (metaGasto == null) {
            throw new IllegalArgumentException("Meta de gasto não pode ser nula");
        }
        if (metaGasto.getNome() == null || metaGasto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da meta de gasto é obrigatório");
        }

        if (metaGasto.getValor() == null) {
            throw new IllegalArgumentException("Valor da meta de gasto é obrigatório");
        }

        if (metaGasto.getFkUsuario() == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório");
        }
    }
}

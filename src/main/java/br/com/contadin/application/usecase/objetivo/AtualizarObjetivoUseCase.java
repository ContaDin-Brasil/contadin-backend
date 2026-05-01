package br.com.contadin.application.usecase.objetivo;

import br.com.contadin.application.port.in.objetivo.AtualizarObjetivoInputPort;
import br.com.contadin.application.port.out.ObjetivoRepository;
import br.com.contadin.domain.model.Objetivo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtualizarObjetivoUseCase implements AtualizarObjetivoInputPort {

        private final ObjetivoRepository objetivoRepository;

        @Override
        public Objetivo execute(UUID id, Objetivo objetivo) {
            if (id == null) {
                throw new IllegalArgumentException("ID do objetivo é obrigatório para atualização");
            }

            Objetivo existente = objetivoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Objetivo não encontrada"));


            Objetivo.ObjetivoBuilder builder = Objetivo.builder()
                    .id(existente.getId())
                    .nome(objetivo.getNome() != null ? objetivo.getNome() : existente.getNome())
                    .valor(objetivo.getValor() != null ? objetivo.getValor() : existente.getValor())
                    .fkUsuario(existente.getFkUsuario())
                    .fkCategoria(objetivo.getFkCategoria() != null ? objetivo.getFkCategoria() : existente.getFkCategoria())
                    .dataFimObjetivo(objetivo.getDataFimObjetivo() != null ? objetivo.getDataFimObjetivo() : existente.getDataFimObjetivo())
                    .atualizadoEm(LocalDateTime.now())
                    .criadoEm(existente.getCriadoEm());

            Objetivo toSave = builder.build();
            return objetivoRepository.save(toSave);

        }
}

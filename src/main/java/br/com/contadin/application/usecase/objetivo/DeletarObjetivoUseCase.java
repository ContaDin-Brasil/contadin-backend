package br.com.contadin.application.usecase.objetivo;

import br.com.contadin.application.port.in.objetivo.DeletarObjetivoInputPort;
import br.com.contadin.application.port.out.ObjetivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletarObjetivoUseCase implements DeletarObjetivoInputPort {

    private final ObjetivoRepository objetivoRepository;

    @Override
    public void execute(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do objetivo é obrigatório para exclusão");
        }

        objetivoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Objetivo não encontrado"));

        objetivoRepository.deleteById(id);
    }
}

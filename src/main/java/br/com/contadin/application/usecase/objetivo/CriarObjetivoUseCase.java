package br.com.contadin.application.usecase.objetivo;

import br.com.contadin.application.port.in.objetivo.CriarObjetivoInputPort;
import br.com.contadin.application.port.out.ObjetivoRepository;
import br.com.contadin.domain.model.Objetivo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CriarObjetivoUseCase implements CriarObjetivoInputPort {
    private final ObjetivoRepository objetivoRepository;

    @Override
    public Objetivo execute(Objetivo objetivo) {
        validarCampos(objetivo);

        LocalDateTime now = LocalDateTime.now();

        Objetivo.ObjetivoBuilder builder = Objetivo.builder()
                .nome(objetivo.getNome())
                .valor(objetivo.getValor())
                .dataFimObjetivo(objetivo.getDataFimObjetivo())
                .fkUsuario(objetivo.getFkUsuario())
                .fkCategoria(objetivo.getFkCategoria())
                .atualizadoEm(now)
                .criadoEm(now);


        Objetivo toSave = builder.build();
        return objetivoRepository.save(toSave);
    }

    public void validarCampos(Objetivo objetivo) {
        if (objetivo == null) {
            throw new IllegalArgumentException("Objetivo não pode ser nulo");
        }
        if (objetivo.getNome() == null || objetivo.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do objetivo é obrigatório");
        }

        if (objetivo.getValor() == null) {
            throw new IllegalArgumentException("Valor do objetivo é obrigatório");
        }

        if (objetivo.getFkUsuario() == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório");
        }
    }
}

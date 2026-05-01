package br.com.contadin.application.usecase.objetivo;

import br.com.contadin.application.port.in.objetivo.BuscarObjetivoInputPort;
import br.com.contadin.application.port.out.ObjetivoRepository;
import br.com.contadin.domain.model.Objetivo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarObjetivoUseCase implements BuscarObjetivoInputPort {

    private final ObjetivoRepository objetivoRepository;

    @Override
    public List<Objetivo> execute(UUID fkUsuario) {
        if (fkUsuario == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório para busca");
        }

        return objetivoRepository.findByUsuario(fkUsuario);
    }

    @Override
    public Objetivo executeBuscarPorId(UUID objetivoId) {
        if (objetivoId == null) {
            throw new IllegalArgumentException("ID do bjetivo é obrigatório para busca");
        }

        return objetivoRepository.findById(objetivoId)
                .orElseThrow(() -> new IllegalArgumentException("Objetivo não encontrado"));

    }

    @Override
    public List<Objetivo> executeBuscarPorNome(String nome, UUID fkUsuario) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do objetivo é obrigatório para busca");
        }
        if (fkUsuario == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório para busca");
        }

        return objetivoRepository.findByNomeAndUsuario(nome, fkUsuario);
    }
}

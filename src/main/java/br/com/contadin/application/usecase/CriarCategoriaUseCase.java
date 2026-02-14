package br.com.contadin.application.usecase;

import br.com.contadin.application.dto.categoria.CategoriaRequest;
import br.com.contadin.application.port.in.CriarCategoriaInputPort;
import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.domain.model.Categoria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class CriarCategoriaUseCase implements CriarCategoriaInputPort {
    private CategoriaRepository categoriaRepository;

    public Categoria execute(CategoriaRequest request) {
        Categoria categoria = new Categoria(request);
        return categoriaRepository.save(categoria);
    }
}

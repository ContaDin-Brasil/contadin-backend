package br.com.contadin.application.usecase.categoria;

import br.com.contadin.application.port.in.categoria.CriarCategoriaInputPort;
import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.domain.model.Categoria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CriarCategoriaUseCase implements CriarCategoriaInputPort {

    private final CategoriaRepository categoriaRepository;

    @Override
    public Categoria execute(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }
}

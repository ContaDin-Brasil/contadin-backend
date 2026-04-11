package br.com.contadin.application.usecase.categoria;

import br.com.contadin.application.port.in.categoria.DeletarCategoriaInputPort;
import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.domain.exception.categoria.CategoriaInvalidaException;
import br.com.contadin.domain.exception.categoria.CategoriaNaoEncontradaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletarCategoriaUseCase implements DeletarCategoriaInputPort {

	private final CategoriaRepository categoriaRepository;

	@Override
	public void execute(UUID id) {
		if (id == null) {
			throw new CategoriaInvalidaException("ID da categoria é obrigatório para deleção.");
		}

		categoriaRepository.findByIdIncludingInactive(id)
				.orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada."));

		categoriaRepository.deleteById(id);
	}
}

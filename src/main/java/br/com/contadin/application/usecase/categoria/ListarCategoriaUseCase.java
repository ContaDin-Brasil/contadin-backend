package br.com.contadin.application.usecase.categoria;

import br.com.contadin.application.port.in.categoria.BuscarCategoriaInputPort;
import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.domain.exception.categoria.CategoriaInvalidaException;
import br.com.contadin.domain.exception.categoria.CategoriaNaoEncontradaException;
import br.com.contadin.domain.model.Categoria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListarCategoriaUseCase implements BuscarCategoriaInputPort {

	private final CategoriaRepository categoriaRepository;

	@Override
	public List<Categoria> execute(UUID fkUsuario) {
		if (fkUsuario == null) {
			throw new CategoriaInvalidaException("fkUsuario é obrigatório para buscar categorias.");
		}

		return categoriaRepository.findByUsuario(fkUsuario);
	}

	@Override
	public Categoria executeBuscarPorId(UUID categoriaId) {
		if (categoriaId == null) {
			throw new CategoriaInvalidaException("ID da categoria é obrigatório para busca.");
		}

		return categoriaRepository.findById(categoriaId)
				.orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada."));
	}

	@Override
	public List<Categoria> executeBuscarPorNome(String nome, UUID fkUsuario) {
		if (fkUsuario == null) {
			throw new CategoriaInvalidaException("fkUsuario é obrigatório para buscar categorias por nome.");
		}

		if (nome == null || nome.isBlank()) {
			throw new CategoriaInvalidaException("Nome é obrigatório para buscar categorias por nome.");
		}

		return categoriaRepository.findByNomeAndUsuario(nome.trim(), fkUsuario);
	}
}

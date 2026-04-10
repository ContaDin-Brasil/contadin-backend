package br.com.contadin.application.usecase.categoria;

import br.com.contadin.application.port.in.categoria.AtualizarCategoriaInputPort;
import br.com.contadin.application.port.out.CategoriaRepository;
import br.com.contadin.domain.exception.categoria.CategoriaInvalidaException;
import br.com.contadin.domain.exception.categoria.CategoriaNaoEncontradaException;
import br.com.contadin.domain.model.Categoria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtualizarCategoriaUseCase implements AtualizarCategoriaInputPort {

    private final CategoriaRepository categoriaRepository;

    @Override
    public Categoria execute(UUID id, Categoria categoria) {
        if (id == null) {
            throw new CategoriaInvalidaException("ID da categoria é obrigatório para atualização.");
        }

        if (categoria == null) {
            throw new CategoriaInvalidaException("Dados da categoria são obrigatórios para atualização.");
        }

        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada."));

        LocalDateTime now = LocalDateTime.now();

        Categoria.CategoriaBuilder builder = Categoria.builder()
                .id(existente.getId())
                .nome(categoria.getNome() != null ? categoria.getNome() : existente.getNome())
                .icone(categoria.getIcone() != null ? categoria.getIcone() : existente.getIcone())
                .cor(categoria.getCor() != null ? categoria.getCor() : existente.getCor())
                .tipo(categoria.getTipo() != null ? categoria.getTipo() : existente.getTipo())
                .fkUsuario(existente.getFkUsuario())
                .criadoEm(existente.getCriadoEm())
                .atualizadoEm(now);

        return categoriaRepository.save(builder.build());
    }
}
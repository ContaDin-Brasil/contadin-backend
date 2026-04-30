package br.com.contadin.application.usecase.categoria;

import br.com.contadin.application.port.in.categoria.AlternarStatusCategoriaInputPort;
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
public class AlternarStatusCategoriaUseCase implements AlternarStatusCategoriaInputPort {

    private final CategoriaRepository categoriaRepository;

    @Override
    public void execute(UUID id) {
        if (id == null) {
            throw new CategoriaInvalidaException("ID da categoria é obrigatório para alternar status.");
        }

        Categoria existente = categoriaRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada."));

        boolean novoStatus = !Boolean.TRUE.equals(existente.getAtivo());

        Categoria categoriaAtualizada = Categoria.builder()
                .id(existente.getId())
                .nome(existente.getNome())
                .icone(existente.getIcone())
                .cor(existente.getCor())
                .tipo(existente.getTipo())
                .fkUsuario(existente.getFkUsuario())
                .ativo(novoStatus)
                .criadoEm(existente.getCriadoEm())
                .atualizadoEm(LocalDateTime.now())
                .build();

        categoriaRepository.save(categoriaAtualizada);
    }
}
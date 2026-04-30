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
        Categoria novaCategoria = Categoria.builder()
                .nome(categoria.getNome())
                .icone(categoria.getIcone())
                .cor(categoria.getCor())
                .tipo(categoria.getTipo())
                .fkUsuario(categoria.getFkUsuario())
                .ativo(categoria.getAtivo() != null ? categoria.getAtivo() : true)
                .criadoEm(categoria.getCriadoEm())
                .atualizadoEm(categoria.getAtualizadoEm())
                .build();

        return categoriaRepository.save(novaCategoria);
    }
}

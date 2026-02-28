package br.com.contadin.application.usecase.instituicao;

import br.com.contadin.application.port.in.instituicao.BuscarInstituicaoInputPort;
import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.domain.model.Instituicao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuscarInstituicaoUseCase implements BuscarInstituicaoInputPort {

    private final InstituicaoRepository instituicaoRepository;

    @Override
    public Instituicao executeBuscarPorId(Integer instituicaoId) {
        return instituicaoRepository.findById(instituicaoId)
                .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada"));
    }

    @Override
    public List<Instituicao> execute(Integer fkUsuario) {
        return instituicaoRepository.findAtivasByUsuario(fkUsuario);
    }

    @Override
    public List<Instituicao> executeBuscarPorNome(Integer fkUsuario, String nome) {
        return instituicaoRepository.findByNomeAndUsuario(fkUsuario, nome);
    }

}

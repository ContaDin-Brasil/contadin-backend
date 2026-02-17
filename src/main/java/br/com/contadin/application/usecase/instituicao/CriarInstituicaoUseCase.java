package br.com.contadin.application.usecase.instituicao;

import br.com.contadin.application.port.in.instituicao.CriarInstituicaoInputPort;
import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.domain.model.Instituicao;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;


@RequiredArgsConstructor
public class CriarInstituicaoUseCase implements CriarInstituicaoInputPort {
    private final InstituicaoRepository instituicaoRepository;

    @Override
    public Instituicao execute(Instituicao instituicao) {

        validarCampos(instituicao);

        LocalDateTime now = LocalDateTime.now();

        Instituicao.InstituicaoBuilder builder = Instituicao.builder()
                .nome(instituicao.getNome())
                .icone(instituicao.getIcone())
                .cor(instituicao.getCor())
                .fkUsuario(instituicao.getFkUsuario())
                .ativo(true)
                .criadoEm(now)
                .atualizadoEm(now);

        Instituicao toSave = builder.build();
        return instituicaoRepository.save(toSave);
    }


    public void validarCampos(Instituicao instituicao) {
        if (instituicao == null) {
            throw new IllegalArgumentException("Instituição não pode ser nula");
        }
        if (instituicao.getNome() == null || instituicao.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da instituição é obrigatório");
        }

        if (instituicao.getFkUsuario() == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório");
        }

        if (instituicao.getCor() == null || instituicao.getCor().trim().isEmpty()) {
            throw new IllegalArgumentException("Cor da instituição é obrigatória");
        }

        if (instituicao.getIcone() == null || instituicao.getIcone().trim().isEmpty()) {
            throw new IllegalArgumentException("Ícone da instituição é obrigatório");
        }
    }

}

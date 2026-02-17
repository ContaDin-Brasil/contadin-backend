package br.com.contadin.application.usecase.instituicao;

import br.com.contadin.application.port.in.instituicao.AtualizarInstituicaoInputPort;
import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.domain.model.Instituicao;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class AtualizarInstituicaoUseCase implements AtualizarInstituicaoInputPort {

    private final InstituicaoRepository instituicaoRepository;

     @Override
    public Instituicao execute(Instituicao instituicao) {
         if (instituicao == null || instituicao.getId() == null) {
             throw new IllegalArgumentException("ID da instituição é obrigatório para atualização");
         }

         Instituicao existente = instituicaoRepository.findById(instituicao.getId())
                 .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada"));

         LocalDateTime now = LocalDateTime.now();

         Instituicao.InstituicaoBuilder builder = Instituicao.builder()
                 .id(existente.getId())
                 .nome(instituicao.getNome() != null ? instituicao.getNome() : existente.getNome())
                 .ativo(existente.isAtivo())
                 .cor(instituicao.getCor() != null ? instituicao.getCor() : existente.getCor())
                 .icone(instituicao.getIcone() != null ? instituicao.getIcone() : existente.getIcone())
                 .criadoEm(existente.getCriadoEm())
                 .atualizadoEm(now);

         Instituicao toSave = builder.build();
         return instituicaoRepository.save(toSave);

     }

}

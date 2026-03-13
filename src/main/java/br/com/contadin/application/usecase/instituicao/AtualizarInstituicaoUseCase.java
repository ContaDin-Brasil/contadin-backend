package br.com.contadin.application.usecase.instituicao;

import br.com.contadin.application.port.in.instituicao.AtualizarInstituicaoInputPort;
import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.domain.model.Instituicao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AtualizarInstituicaoUseCase implements AtualizarInstituicaoInputPort {

    private final InstituicaoRepository instituicaoRepository;

     @Override
    public Instituicao execute(Integer id, Instituicao instituicao) {
         if (id == null) {
             throw new IllegalArgumentException("ID da instituição é obrigatório para atualização");
         }

         Instituicao existente = instituicaoRepository.findById(id)
                 .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada"));

         LocalDateTime now = LocalDateTime.now();

         Instituicao.InstituicaoBuilder builder = Instituicao.builder()
                 .id(id)
                 .nome(instituicao.getNome() != null ? instituicao.getNome() : existente.getNome())
                 .ativo(instituicao.getAtivo() != null ? instituicao.getAtivo() : existente.getAtivo())
                 .tipo(instituicao.getTipo() != null ? instituicao.getTipo() : existente.getTipo())
                 .cor(instituicao.getCor() != null ? instituicao.getCor() : existente.getCor())
                 .icone(instituicao.getIcone() != null ? instituicao.getIcone() : existente.getIcone())
                 .fkUsuario(existente.getFkUsuario())
                 .criadoEm(existente.getCriadoEm())
                 .atualizadoEm(now);

         Instituicao toSave = builder.build();
         return instituicaoRepository.save(toSave);

     }

}

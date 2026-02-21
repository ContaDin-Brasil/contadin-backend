package br.com.contadin.application.usecase.instituicao;

import br.com.contadin.application.port.in.instituicao.DesativarInstituicaoInputPort;
import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.domain.model.Instituicao;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class DesativarInstituicaoUseCase implements DesativarInstituicaoInputPort {

        private final InstituicaoRepository instituicaoRepository;

        @Override
        public void execute(Integer id) {

            Instituicao existente = instituicaoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada"));

            LocalDateTime now = LocalDateTime.now();

            Instituicao.InstituicaoBuilder builder = Instituicao.builder()
                    .id(existente.getId())
                    .nome(existente.getNome())
                    .ativo(false)
                    .cor(existente.getCor())
                    .icone(existente.getIcone())
                    .criadoEm(existente.getCriadoEm())
                    .atualizadoEm(now);

            Instituicao toSave = builder.build();
            instituicaoRepository.save(toSave);
        }

}

package br.com.contadin.application.usecase.instituicao;

import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.domain.enums.TipoInstituicao;
import br.com.contadin.domain.model.Instituicao;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DesativarInstituicaoUseCase")
class DesativarInstituicaoUseCaseTest {

    @Mock
    private InstituicaoRepository instituicaoRepository;

    @InjectMocks
    private DesativarInstituicaoUseCase useCase;

    private final UUID USUARIO_ID = UUID.randomUUID();
    private final LocalDateTime CRIADO_EM = LocalDateTime.of(2024, 3, 15, 10, 0);

    private Instituicao instituicaoAtiva(UUID id) {
        return Instituicao.builder()
                .id(id)
                .nome("Nubank")
                .icone("nu-icon")
                .cor("#8A05BE")
                .tipo(TipoInstituicao.BANCO)
                .fkUsuario(USUARIO_ID)
                .saldoInicial(new BigDecimal("1000.00"))
                .ativo(true)
                .criadoEm(CRIADO_EM)
                .build();
    }

    @Nested
    @DisplayName("execute() - busca da instituição")
    class BuscaInstituicao {

        @Test
        @DisplayName("deve lançar IllegalArgumentException quando instituição não for encontrada")
        void deveLancarExcecaoQuandoNaoEncontrada() {
            UUID id = UUID.randomUUID();
            when(instituicaoRepository.findById(id)).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(id));
            assertTrue(ex.getMessage().contains("Instituição não encontrada"));
        }
    }

    @Nested
    @DisplayName("execute() - happy path")
    class HappyPath {

        @Test
        @DisplayName("deve salvar instituição com ativo=false")
        void deveSalvarComAtivoFalse() {
            UUID id = UUID.randomUUID();
            Instituicao inst = instituicaoAtiva(id);
            when(instituicaoRepository.findById(id)).thenReturn(Optional.of(inst));

            useCase.execute(id);

            verify(instituicaoRepository).save(argThat(i -> Boolean.FALSE.equals(i.getAtivo())));
        }

        @Test
        @DisplayName("deve preservar nome, icone, cor, tipo e fkUsuario ao desativar")
        void devePreservarCamposDaInstituicao() {
            UUID id = UUID.randomUUID();
            Instituicao inst = instituicaoAtiva(id);
            when(instituicaoRepository.findById(id)).thenReturn(Optional.of(inst));

            useCase.execute(id);

            verify(instituicaoRepository).save(argThat(i ->
                    "Nubank".equals(i.getNome())
                            && "nu-icon".equals(i.getIcone())
                            && "#8A05BE".equals(i.getCor())
                            && i.getTipo() == TipoInstituicao.BANCO
                            && USUARIO_ID.equals(i.getFkUsuario())
            ));
        }

        @Test
        @DisplayName("deve preservar o id da instituição")
        void devePreservarId() {
            UUID id = UUID.randomUUID();
            Instituicao inst = instituicaoAtiva(id);
            when(instituicaoRepository.findById(id)).thenReturn(Optional.of(inst));

            useCase.execute(id);

            verify(instituicaoRepository).save(argThat(i -> id.equals(i.getId())));
        }

        @Test
        @DisplayName("deve preservar criadoEm ao desativar")
        void devePreservarCriadoEm() {
            UUID id = UUID.randomUUID();
            Instituicao inst = instituicaoAtiva(id);
            when(instituicaoRepository.findById(id)).thenReturn(Optional.of(inst));

            useCase.execute(id);

            verify(instituicaoRepository).save(argThat(i -> CRIADO_EM.equals(i.getCriadoEm())));
        }

        @Test
        @DisplayName("deve chamar repository.save() exatamente uma vez")
        void deveChamarSaveUmaVez() {
            UUID id = UUID.randomUUID();
            Instituicao inst = instituicaoAtiva(id);
            when(instituicaoRepository.findById(id)).thenReturn(Optional.of(inst));

            useCase.execute(id);

            verify(instituicaoRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("não deve retornar valor (método void)")
        void naoDeveRetornarValor() {
            UUID id = UUID.randomUUID();
            Instituicao inst = instituicaoAtiva(id);
            when(instituicaoRepository.findById(id)).thenReturn(Optional.of(inst));

            assertDoesNotThrow(() -> useCase.execute(id));
        }
    }
}

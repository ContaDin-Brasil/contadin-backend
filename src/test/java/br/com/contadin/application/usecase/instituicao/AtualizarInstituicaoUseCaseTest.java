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
@DisplayName("AtualizarInstituicaoUseCase")
class AtualizarInstituicaoUseCaseTest {

    @Mock
    private InstituicaoRepository instituicaoRepository;

    @InjectMocks
    private AtualizarInstituicaoUseCase useCase;

    private final UUID USUARIO_ID = UUID.randomUUID();
    private final LocalDateTime CRIADO_EM = LocalDateTime.of(2024, 1, 10, 12, 0);

    private Instituicao existente(UUID id) {
        return Instituicao.builder()
                .id(id)
                .nome("Nome Original")
                .icone("icon-original")
                .cor("#000000")
                .tipo(TipoInstituicao.BANCO)
                .fkUsuario(USUARIO_ID)
                .saldoInicial(new BigDecimal("500.00"))
                .ativo(true)
                .criadoEm(CRIADO_EM)
                .build();
    }

    @Nested
    @DisplayName("execute() - validação de id")
    class ValidacaoId {

        @Test
        @DisplayName("id null → IllegalArgumentException")
        void idNulo() {
            Instituicao patch = Instituicao.builder().nome("Novo Nome").build();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(null, patch));
            assertEquals("ID da instituição é obrigatório para atualização", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("execute() - busca da instituição")
    class BuscaInstituicao {

        @Test
        @DisplayName("id não encontrado → IllegalArgumentException")
        void idNaoEncontrado() {
            UUID id = UUID.randomUUID();
            when(instituicaoRepository.findById(id)).thenReturn(Optional.empty());
            Instituicao patch = Instituicao.builder().nome("Novo").build();

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(id, patch));
            assertEquals("Instituição não encontrada", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("execute() - happy path e merge de campos")
    class HappyPath {

        @Test
        @DisplayName("deve atualizar todos os campos fornecidos no patch")
        void deveAtualizarTodosCampos() {
            UUID id = UUID.randomUUID();
            Instituicao inst = existente(id);
            when(instituicaoRepository.findById(id)).thenReturn(Optional.of(inst));
            when(instituicaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Instituicao patch = Instituicao.builder()
                    .nome("Nome Atualizado")
                    .icone("icon-novo")
                    .cor("#FFFFFF")
                    .tipo(TipoInstituicao.VALE)
                    .ativo(false)
                    .build();

            Instituicao resultado = useCase.execute(id, patch);

            assertEquals("Nome Atualizado", resultado.getNome());
            assertEquals("icon-novo", resultado.getIcone());
            assertEquals("#FFFFFF", resultado.getCor());
            assertEquals(TipoInstituicao.VALE, resultado.getTipo());
            assertFalse(resultado.getAtivo());
        }

        @Test
        @DisplayName("deve manter campos existentes quando patch traz null")
        void deveManterCamposExistentesQuandoPatchNull() {
            UUID id = UUID.randomUUID();
            Instituicao inst = existente(id);
            when(instituicaoRepository.findById(id)).thenReturn(Optional.of(inst));
            when(instituicaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Instituicao patch = Instituicao.builder()
                    .nome(null).icone(null).cor(null).tipo(null).ativo(null)
                    .build();

            Instituicao resultado = useCase.execute(id, patch);

            assertEquals("Nome Original", resultado.getNome());
            assertEquals("icon-original", resultado.getIcone());
            assertEquals("#000000", resultado.getCor());
            assertEquals(TipoInstituicao.BANCO, resultado.getTipo());
            assertTrue(resultado.getAtivo());
        }

        @Test
        @DisplayName("deve preservar saldoInicial, fkUsuario e criadoEm do existente")
        void devePreservarCamposImutaveis() {
            UUID id = UUID.randomUUID();
            Instituicao inst = existente(id);
            when(instituicaoRepository.findById(id)).thenReturn(Optional.of(inst));
            when(instituicaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Instituicao patch = Instituicao.builder()
                    .nome("Patch Nome")
                    .saldoInicial(new BigDecimal("9999.00"))
                    .fkUsuario(UUID.randomUUID())
                    .criadoEm(LocalDateTime.now())
                    .build();

            Instituicao resultado = useCase.execute(id, patch);

            assertEquals(new BigDecimal("500.00"), resultado.getSaldoInicial());
            assertEquals(USUARIO_ID, resultado.getFkUsuario());
            assertEquals(CRIADO_EM, resultado.getCriadoEm());
        }

        @Test
        @DisplayName("deve manter o id original")
        void deveManterIdOriginal() {
            UUID id = UUID.randomUUID();
            Instituicao inst = existente(id);
            when(instituicaoRepository.findById(id)).thenReturn(Optional.of(inst));
            when(instituicaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Instituicao patch = Instituicao.builder().nome("Novo").build();
            Instituicao resultado = useCase.execute(id, patch);

            assertEquals(id, resultado.getId());
        }

        @Test
        @DisplayName("deve chamar repository.save() exatamente uma vez")
        void deveChamarSaveUmaVez() {
            UUID id = UUID.randomUUID();
            Instituicao inst = existente(id);
            when(instituicaoRepository.findById(id)).thenReturn(Optional.of(inst));
            when(instituicaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            useCase.execute(id, Instituicao.builder().nome("X").build());

            verify(instituicaoRepository, times(1)).save(any());
        }
    }
}

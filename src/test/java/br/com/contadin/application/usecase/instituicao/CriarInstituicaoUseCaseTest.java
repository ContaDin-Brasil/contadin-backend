package br.com.contadin.application.usecase.instituicao;

import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.domain.enums.TipoInstituicao;
import br.com.contadin.domain.model.Instituicao;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CriarInstituicaoUseCase")
class CriarInstituicaoUseCaseTest {

    @Mock
    private InstituicaoRepository instituicaoRepository;

    @InjectMocks
    private CriarInstituicaoUseCase useCase;

    private Instituicao instituicaoValida() {
        return Instituicao.builder()
                .nome("Banco Inter")
                .icone("bank-icon")
                .cor("#FF6B00")
                .fkUsuario(UUID.randomUUID())
                .tipo(TipoInstituicao.BANCO)
                .build();
    }

    @Nested
    @DisplayName("execute() - happy path")
    class Execute {

        @Test
        @DisplayName("deve salvar instituição com saldoInicial=ZERO e ativo=true")
        void deveSalvarInstituicaoValida() {
            Instituicao input = instituicaoValida();
            Instituicao salva = Instituicao.builder()
                    .id(UUID.randomUUID())
                    .nome(input.getNome())
                    .icone(input.getIcone())
                    .cor(input.getCor())
                    .fkUsuario(input.getFkUsuario())
                    .tipo(input.getTipo())
                    .saldoInicial(BigDecimal.ZERO)
                    .ativo(true)
                    .build();

            when(instituicaoRepository.save(any())).thenReturn(salva);

            Instituicao resultado = useCase.execute(input);

            assertNotNull(resultado);
            verify(instituicaoRepository).save(argThat(i ->
                    i.getSaldoInicial().compareTo(BigDecimal.ZERO) == 0
                            && Boolean.TRUE.equals(i.getAtivo())
                            && i.getNome().equals(input.getNome())
                            && i.getCor().equals(input.getCor())
                            && i.getIcone().equals(input.getIcone())
                            && i.getFkUsuario().equals(input.getFkUsuario())
                            && i.getTipo() == TipoInstituicao.BANCO
            ));
        }

        @Test
        @DisplayName("deve salvar corretamente com tipo VALE")
        void deveSalvarComTipoVale() {
            Instituicao input = Instituicao.builder()
                    .nome("VR Alimentação")
                    .icone("vr-icon")
                    .cor("#00CC00")
                    .fkUsuario(UUID.randomUUID())
                    .tipo(TipoInstituicao.VALE)
                    .build();
            when(instituicaoRepository.save(any())).thenReturn(input);

            Instituicao resultado = useCase.execute(input);

            assertNotNull(resultado);
            verify(instituicaoRepository).save(any());
        }

        @Test
        @DisplayName("deve retornar o objeto retornado pelo repository")
        void deveRetornarObjetoDoRepository() {
            Instituicao input = instituicaoValida();
            UUID idGerado = UUID.randomUUID();
            Instituicao salva = Instituicao.builder()
                    .id(idGerado)
                    .nome(input.getNome())
                    .icone(input.getIcone())
                    .cor(input.getCor())
                    .fkUsuario(input.getFkUsuario())
                    .tipo(input.getTipo())
                    .saldoInicial(BigDecimal.ZERO)
                    .ativo(true)
                    .build();

            when(instituicaoRepository.save(any())).thenReturn(salva);

            Instituicao resultado = useCase.execute(input);

            assertEquals(idGerado, resultado.getId());
        }
    }

    @Nested
    @DisplayName("validarCampos() - erros de validação")
    class ValidarCampos {

        @Test
        @DisplayName("instituição null → IllegalArgumentException")
        void instituicaoNula() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(null));
            assertEquals("Instituição não pode ser nula", ex.getMessage());
        }

        @Test
        @DisplayName("nome null → IllegalArgumentException")
        void nomeNulo() {
            Instituicao i = Instituicao.builder()
                    .nome(null).icone("x").cor("#FFF")
                    .fkUsuario(UUID.randomUUID()).tipo(TipoInstituicao.BANCO).build();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(i));
            assertEquals("Nome da instituição é obrigatório", ex.getMessage());
        }

        @Test
        @DisplayName("nome blank → IllegalArgumentException")
        void nomeBlank() {
            Instituicao i = Instituicao.builder()
                    .nome("   ").icone("x").cor("#FFF")
                    .fkUsuario(UUID.randomUUID()).tipo(TipoInstituicao.BANCO).build();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(i));
            assertEquals("Nome da instituição é obrigatório", ex.getMessage());
        }

        @Test
        @DisplayName("fkUsuario null → IllegalArgumentException")
        void fkUsuarioNulo() {
            Instituicao i = Instituicao.builder()
                    .nome("Banco").icone("x").cor("#FFF")
                    .fkUsuario(null).tipo(TipoInstituicao.BANCO).build();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(i));
            assertEquals("ID do usuário é obrigatório", ex.getMessage());
        }

        @Test
        @DisplayName("cor null → IllegalArgumentException")
        void corNula() {
            Instituicao i = Instituicao.builder()
                    .nome("Banco").icone("x").cor(null)
                    .fkUsuario(UUID.randomUUID()).tipo(TipoInstituicao.BANCO).build();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(i));
            assertEquals("Cor da instituição é obrigatória", ex.getMessage());
        }

        @Test
        @DisplayName("cor blank → IllegalArgumentException")
        void corBlank() {
            Instituicao i = Instituicao.builder()
                    .nome("Banco").icone("x").cor("   ")
                    .fkUsuario(UUID.randomUUID()).tipo(TipoInstituicao.BANCO).build();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(i));
            assertEquals("Cor da instituição é obrigatória", ex.getMessage());
        }

        @Test
        @DisplayName("icone null → IllegalArgumentException")
        void iconeNulo() {
            Instituicao i = Instituicao.builder()
                    .nome("Banco").icone(null).cor("#FFF")
                    .fkUsuario(UUID.randomUUID()).tipo(TipoInstituicao.BANCO).build();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(i));
            assertEquals("Ícone da instituição é obrigatório", ex.getMessage());
        }

        @Test
        @DisplayName("icone blank → IllegalArgumentException")
        void iconeBlank() {
            Instituicao i = Instituicao.builder()
                    .nome("Banco").icone("   ").cor("#FFF")
                    .fkUsuario(UUID.randomUUID()).tipo(TipoInstituicao.BANCO).build();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(i));
            assertEquals("Ícone da instituição é obrigatório", ex.getMessage());
        }

        @Test
        @DisplayName("tipo null → IllegalArgumentException")
        void tipoNulo() {
            Instituicao i = Instituicao.builder()
                    .nome("Banco").icone("x").cor("#FFF")
                    .fkUsuario(UUID.randomUUID()).tipo(null).build();
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.execute(i));
            assertEquals("Tipo da instituição é obrigatório", ex.getMessage());
        }
    }
}

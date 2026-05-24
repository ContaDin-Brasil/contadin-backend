package br.com.contadin.application.usecase.instituicao;

import br.com.contadin.application.port.out.InstituicaoRepository;
import br.com.contadin.domain.enums.TipoInstituicao;
import br.com.contadin.domain.model.Instituicao;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarInstituicaoUseCase")
class BuscarInstituicaoUseCaseTest {

    @Mock
    private InstituicaoRepository instituicaoRepository;

    @InjectMocks
    private BuscarInstituicaoUseCase useCase;

    private Instituicao criarInstituicao(UUID id) {
        return Instituicao.builder()
                .id(id)
                .nome("Banco do Brasil")
                .icone("bb-icon")
                .cor("#FFCD00")
                .fkUsuario(UUID.randomUUID())
                .tipo(TipoInstituicao.BANCO)
                .ativo(true)
                .build();
    }

    @Nested
    @DisplayName("executeBuscarPorId()")
    class ExecuteBuscarPorId {

        @Test
        @DisplayName("deve retornar instituição quando encontrada")
        void deveRetornarInstituicaoEncontrada() {
            UUID id = UUID.randomUUID();
            Instituicao instituicao = criarInstituicao(id);
            when(instituicaoRepository.findById(id)).thenReturn(Optional.of(instituicao));

            Instituicao resultado = useCase.executeBuscarPorId(id);

            assertNotNull(resultado);
            assertEquals(id, resultado.getId());
            assertEquals("Banco do Brasil", resultado.getNome());
        }

        @Test
        @DisplayName("deve lançar IllegalArgumentException quando não encontrada")
        void deveLancarExcecaoQuandoNaoEncontrada() {
            UUID id = UUID.randomUUID();
            when(instituicaoRepository.findById(id)).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> useCase.executeBuscarPorId(id));
            assertEquals("Instituição não encontrada", ex.getMessage());
        }

        @Test
        @DisplayName("deve chamar repository com o id correto")
        void deveChamarRepositoryComIdCorreto() {
            UUID id = UUID.randomUUID();
            Instituicao instituicao = criarInstituicao(id);
            when(instituicaoRepository.findById(id)).thenReturn(Optional.of(instituicao));

            useCase.executeBuscarPorId(id);

            verify(instituicaoRepository).findById(id);
        }
    }

    @Nested
    @DisplayName("execute(UUID fkUsuario) - listar ativas por usuário")
    class Execute {

        @Test
        @DisplayName("deve retornar lista de instituições ativas do usuário")
        void deveRetornarListaDeInstituicoes() {
            UUID fkUsuario = UUID.randomUUID();
            List<Instituicao> lista = List.of(
                    criarInstituicao(UUID.randomUUID()),
                    criarInstituicao(UUID.randomUUID())
            );
            when(instituicaoRepository.findAtivasByUsuario(fkUsuario)).thenReturn(lista);

            List<Instituicao> resultado = useCase.execute(fkUsuario);

            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(instituicaoRepository).findAtivasByUsuario(fkUsuario);
        }

        @Test
        @DisplayName("deve retornar lista vazia quando usuário não tem instituições")
        void deveRetornarListaVazia() {
            UUID fkUsuario = UUID.randomUUID();
            when(instituicaoRepository.findAtivasByUsuario(fkUsuario)).thenReturn(List.of());

            List<Instituicao> resultado = useCase.execute(fkUsuario);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("executeBuscarPorNome()")
    class ExecuteBuscarPorNome {

        @Test
        @DisplayName("deve retornar lista filtrada por nome e usuário")
        void deveRetornarListaFiltradaPorNome() {
            UUID fkUsuario = UUID.randomUUID();
            String nome = "Banco";
            List<Instituicao> lista = List.of(criarInstituicao(UUID.randomUUID()));
            when(instituicaoRepository.findByNomeAndUsuario(fkUsuario, nome)).thenReturn(lista);

            List<Instituicao> resultado = useCase.executeBuscarPorNome(fkUsuario, nome);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(instituicaoRepository).findByNomeAndUsuario(fkUsuario, nome);
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não encontrar por nome")
        void deveRetornarListaVaziaQuandoNaoEncontrar() {
            UUID fkUsuario = UUID.randomUUID();
            String nome = "NomeInexistente";
            when(instituicaoRepository.findByNomeAndUsuario(fkUsuario, nome)).thenReturn(List.of());

            List<Instituicao> resultado = useCase.executeBuscarPorNome(fkUsuario, nome);

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }
}

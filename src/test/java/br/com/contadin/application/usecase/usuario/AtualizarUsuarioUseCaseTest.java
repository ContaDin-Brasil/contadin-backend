package br.com.contadin.application.usecase.usuario;

import br.com.contadin.application.exception.usuario.UsuarioNaoEncontradoException;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.domain.valueobject.Email;
import br.com.contadin.domain.valueobject.Telefone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AtualizarUsuarioUseCase useCase;

    private UUID usuarioId;
    private Usuario usuarioExistente;

    @BeforeEach
    void setup() {
        usuarioId = UUID.randomUUID();

        usuarioExistente = Usuario.builder()
                .id(usuarioId)
                .nome("Gustavo")
                .sobrenome("Kohatsu")
                .email(new Email("gustavo@email.com"))
                .senha("senha-hash")
                .telefone(new Telefone("11999999999"))
                .ativo(true)
                .build();
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve atualizar nome, sobrenome e telefone quando informados no patch")
        void deveAtualizarCamposQuandoInformados() {
            Usuario patch = Usuario.builder()
                    .nome("NovoNome")
                    .sobrenome("NovoSobrenome")
                    .telefone(new Telefone("11988888888"))
                    .build();

            Usuario usuarioAtualizado = Usuario.builder()
                    .id(usuarioId)
                    .nome("NovoNome")
                    .build();

            when(usuarioRepository.findById(usuarioId))
                    .thenReturn(Optional.of(usuarioExistente));

            when(usuarioRepository.save(any(Usuario.class)))
                    .thenReturn(usuarioAtualizado);

            Usuario resultado = useCase.execute(usuarioId, patch);

            assertNotNull(resultado);

            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).save(captor.capture());

            Usuario salvo = captor.getValue();
            assertEquals("NovoNome", salvo.getNome());
            assertEquals("NovoSobrenome", salvo.getSobrenome());
            assertEquals("11988888888", salvo.getTelefone().numero());
        }

        @Test
        @DisplayName("Deve manter os valores existentes quando campos do patch forem nulos")
        void deveMantermValoresExistentesQuandoPatchForNulo() {
            Usuario patchVazio = Usuario.builder().build();

            when(usuarioRepository.findById(usuarioId))
                    .thenReturn(Optional.of(usuarioExistente));

            when(usuarioRepository.save(any(Usuario.class)))
                    .thenReturn(usuarioExistente);

            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);

            useCase.execute(usuarioId, patchVazio);

            verify(usuarioRepository).save(captor.capture());

            Usuario salvo = captor.getValue();
            assertEquals("Gustavo", salvo.getNome());
            assertEquals("Kohatsu", salvo.getSobrenome());
            assertEquals("11999999999", salvo.getTelefone().numero());
        }

        @Test
        @DisplayName("Não deve alterar e-mail nem senha independente do patch")
        void naoDeveAlterarEmailNemSenha() {
            Usuario patch = Usuario.builder()
                    .nome("NovoNome")
                    .build();

            when(usuarioRepository.findById(usuarioId))
                    .thenReturn(Optional.of(usuarioExistente));

            when(usuarioRepository.save(any(Usuario.class)))
                    .thenReturn(usuarioExistente);

            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);

            useCase.execute(usuarioId, patch);

            verify(usuarioRepository).save(captor.capture());

            Usuario salvo = captor.getValue();
            assertEquals(usuarioExistente.getEmail(), salvo.getEmail());
            assertEquals(usuarioExistente.getSenha(), salvo.getSenha());
        }

        @Test
        @DisplayName("Deve preservar status ativo do usuário existente")
        void devePreservarStatusAtivo() {
            Usuario patch = Usuario.builder().nome("NovoNome").build();

            when(usuarioRepository.findById(usuarioId))
                    .thenReturn(Optional.of(usuarioExistente));

            when(usuarioRepository.save(any(Usuario.class)))
                    .thenReturn(usuarioExistente);

            ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);

            useCase.execute(usuarioId, patch);

            verify(usuarioRepository).save(captor.capture());

            assertEquals(usuarioExistente.isAtivo(), captor.getValue().isAtivo());
        }

        @Test
        @DisplayName("Deve retornar o usuário salvo pelo repositório")
        void deveRetornarUsuarioSalvoPeloRepositorio() {
            Usuario usuarioSalvo = Usuario.builder()
                    .id(usuarioId)
                    .nome("NovoNome")
                    .email(new Email("gustavo@email.com"))
                    .build();

            when(usuarioRepository.findById(usuarioId))
                    .thenReturn(Optional.of(usuarioExistente));

            when(usuarioRepository.save(any(Usuario.class)))
                    .thenReturn(usuarioSalvo);

            Usuario resultado = useCase.execute(usuarioId, Usuario.builder().nome("NovoNome").build());

            assertEquals(usuarioSalvo, resultado);
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar UsuarioNaoEncontradoException quando usuário não existir")
        void deveLancarExcecaoQuandoUsuarioNaoExistir() {
            when(usuarioRepository.findById(usuarioId))
                    .thenReturn(Optional.empty());

            UsuarioNaoEncontradoException exception = assertThrows(
                    UsuarioNaoEncontradoException.class,
                    () -> useCase.execute(usuarioId, Usuario.builder().build())
            );

            assertEquals("Usuário não encontrado", exception.getMessage());

            verify(usuarioRepository, never()).save(any());
        }
    }
}

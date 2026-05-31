package br.com.contadin.infrastructure.security;

import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UserDetailsServiceImpl service;

    private Usuario usuarioAtivo;

    @BeforeEach
    void setup() {
        usuarioAtivo = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("Gustavo")
                .email(new Email("gustavo@email.com"))
                .senha("senha-hash")
                .ativo(true)
                .build();
    }

    @Nested
    class CasosDeSucesso {

        @Test
        @DisplayName("Deve retornar UserDetails com email e senha quando usuário estiver ativo")
        void deveRetornarUserDetailsParaUsuarioAtivo() {
            when(usuarioRepository.findByEmail("gustavo@email.com"))
                    .thenReturn(Optional.of(usuarioAtivo));

            UserDetails userDetails = service.loadUserByUsername("gustavo@email.com");

            assertNotNull(userDetails);
            assertEquals("gustavo@email.com", userDetails.getUsername());
            assertEquals("senha-hash", userDetails.getPassword());
        }

        @Test
        @DisplayName("Deve retornar UserDetails sem papéis (authorities vazia)")
        void deveRetornarUserDetailsSemPapeis() {
            when(usuarioRepository.findByEmail("gustavo@email.com"))
                    .thenReturn(Optional.of(usuarioAtivo));

            UserDetails userDetails = service.loadUserByUsername("gustavo@email.com");

            assertTrue(userDetails.getAuthorities().isEmpty());
        }

        @Test
        @DisplayName("Deve normalizar email (trim + lowercase) antes de buscar")
        void deveNormalizarEmailAntesDaBusca() {
            when(usuarioRepository.findByEmail("gustavo@email.com"))
                    .thenReturn(Optional.of(usuarioAtivo));

            service.loadUserByUsername("  GUSTAVO@EMAIL.COM  ");

            verify(usuarioRepository).findByEmail("gustavo@email.com");
        }

        @Test
        @DisplayName("Deve retornar conta não expirada, não bloqueada e com credenciais válidas")
        void deveRetornarContaComFlagsCorretos() {
            when(usuarioRepository.findByEmail("gustavo@email.com"))
                    .thenReturn(Optional.of(usuarioAtivo));

            UserDetails userDetails = service.loadUserByUsername("gustavo@email.com");

            assertTrue(userDetails.isAccountNonExpired());
            assertTrue(userDetails.isAccountNonLocked());
            assertTrue(userDetails.isCredentialsNonExpired());
            assertTrue(userDetails.isEnabled());
        }
    }

    @Nested
    class CasosDeErro {

        @Test
        @DisplayName("Deve lançar UsernameNotFoundException quando usuário não existir")
        void deveLancarExcecaoQuandoUsuarioNaoExistir() {
            when(usuarioRepository.findByEmail("inexistente@email.com"))
                    .thenReturn(Optional.empty());

            assertThrows(
                    UsernameNotFoundException.class,
                    () -> service.loadUserByUsername("inexistente@email.com")
            );
        }

        @Test
        @DisplayName("Deve lançar DisabledException quando usuário estiver inativo")
        void deveLancarExcecaoQuandoUsuarioInativo() {
            Usuario usuarioInativo = Usuario.builder()
                    .id(UUID.randomUUID())
                    .nome("Inativo")
                    .email(new Email("inativo@email.com"))
                    .senha("senha-hash")
                    .ativo(false)
                    .build();

            when(usuarioRepository.findByEmail("inativo@email.com"))
                    .thenReturn(Optional.of(usuarioInativo));

            assertThrows(
                    DisabledException.class,
                    () -> service.loadUserByUsername("inativo@email.com")
            );
        }
    }
}

package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.EsqueceuSenhaRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.EsqueceuSenhaResponse;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ReenviarPinRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ReenviarPinResponse;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.RedefinirSenhaRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.RedefinirSenhaResponse;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ValidarPinRequest;
import br.com.contadin.application.dto.tokenRecuperarSenha.recuperarSenha.ValidarPinResponse;
import br.com.contadin.application.dto.usuario.CriarUsuarioRequest;
import br.com.contadin.application.dto.usuario.CriarUsuarioResponse;
import br.com.contadin.application.dto.usuario.auth.AlterarSenhaRequest;
import br.com.contadin.application.dto.usuario.auth.LoginRequest;
import br.com.contadin.application.dto.usuario.auth.LoginResponse;
import br.com.contadin.application.port.in.auth.AlterarSenhaInputPort;
import br.com.contadin.application.port.in.auth.EsqueceuSenhaInputPort;
import br.com.contadin.application.port.in.auth.LoginUseCase;
import br.com.contadin.application.port.in.auth.LogoutInputPort;
import br.com.contadin.application.port.in.auth.ReenviarPinInputPort;
import br.com.contadin.application.port.in.auth.RedefinirSenhaInputPort;
import br.com.contadin.application.port.in.auth.ValidarPinInputPort;
import br.com.contadin.application.port.in.usuario.CriarUsuarioInputPort;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.infrastructure.web.mapper.UsuarioWebMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock LoginUseCase loginUseCase;
    @Mock LogoutInputPort logoutInputPort;
    @Mock CriarUsuarioInputPort criarUsuarioInputPort;
    @Mock AlterarSenhaInputPort alterarSenhaInputPort;
    @Mock EsqueceuSenhaInputPort esqueceuSenhaInputPort;
    @Mock ValidarPinInputPort validarPinInputPort;
    @Mock RedefinirSenhaInputPort redefinirSenhaInputPort;
    @Mock ReenviarPinInputPort reenviarPinInputPort;
    @Mock UsuarioWebMapper usuarioWebMapper;

    @InjectMocks AuthController controller;

    @Test
    void deveRealizarLoginERetornar200() {
        LoginRequest request = new LoginRequest("user@email.com", "Senha@123");
        LoginResponse loginResponse = new LoginResponse("token-jwt", UUID.randomUUID(), "João", "Silva", "user@email.com", true);

        when(loginUseCase.execute(request)).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> result = controller.login(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(loginResponse, result.getBody());
        verify(loginUseCase).execute(request);
    }

    @Test
    void deveRealizarLogoutERetornar204() {
        String authHeader = "Bearer token-jwt";

        ResponseEntity<Void> result = controller.logout(authHeader);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(logoutInputPort).execute(authHeader);
    }

    @Test
    void deveCadastrarUsuarioERetornar201() {
        CriarUsuarioRequest request = new CriarUsuarioRequest("João", "Silva", "joao@email.com", "11999999999", "Senha@123!", true);
        Usuario domainUsuario = Usuario.builder().nome("João").build();
        Usuario usuarioCriado = Usuario.builder().id(UUID.randomUUID()).nome("João").build();
        CriarUsuarioResponse response = new CriarUsuarioResponse(UUID.randomUUID(), "João", "Silva", "joao@email.com", "11999999999", "hash", true, null, null);

        when(usuarioWebMapper.toDomain(request)).thenReturn(domainUsuario);
        when(criarUsuarioInputPort.execute(domainUsuario)).thenReturn(usuarioCriado);
        when(usuarioWebMapper.toResponse(usuarioCriado)).thenReturn(response);

        ResponseEntity<CriarUsuarioResponse> result = controller.cadastrar(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(usuarioWebMapper).toDomain(request);
        verify(criarUsuarioInputPort).execute(domainUsuario);
        verify(usuarioWebMapper).toResponse(usuarioCriado);
    }

    @Test
    void deveAlterarSenhaERetornar204() {
        AlterarSenhaRequest request = new AlterarSenhaRequest(UUID.randomUUID(), "Senha@123!", "Nova@456!", "Nova@456!");

        ResponseEntity<Void> result = controller.alterarSenha(request);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(alterarSenhaInputPort).execute(request);
    }

    @Test
    void deveProcessarEsqueceuSenhaERetornar200() {
        EsqueceuSenhaRequest request = new EsqueceuSenhaRequest("user@email.com");
        EsqueceuSenhaResponse response = new EsqueceuSenhaResponse("PIN enviado");

        when(esqueceuSenhaInputPort.execute(request)).thenReturn(response);

        ResponseEntity<EsqueceuSenhaResponse> result = controller.esqueceuSenha(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(esqueceuSenhaInputPort).execute(request);
    }

    @Test
    void deveValidarPinERetornar200() {
        ValidarPinRequest request = new ValidarPinRequest("user@email.com", "123456");
        ValidarPinResponse response = new ValidarPinResponse("PIN válido");

        when(validarPinInputPort.execute(request)).thenReturn(response);

        ResponseEntity<ValidarPinResponse> result = controller.validarPin(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(validarPinInputPort).execute(request);
    }

    @Test
    void deveRedefinirSenhaERetornar200() {
        RedefinirSenhaRequest request = mock(RedefinirSenhaRequest.class);
        RedefinirSenhaResponse response = new RedefinirSenhaResponse("Senha redefinida");

        when(redefinirSenhaInputPort.execute(request)).thenReturn(response);

        ResponseEntity<RedefinirSenhaResponse> result = controller.redefinirSenha(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(redefinirSenhaInputPort).execute(request);
    }

    @Test
    void deveReenviarPinERetornar200() {
        ReenviarPinRequest request = new ReenviarPinRequest("user@email.com");
        ReenviarPinResponse response = new ReenviarPinResponse("PIN reenviado");

        when(reenviarPinInputPort.execute(request)).thenReturn(response);

        ResponseEntity<ReenviarPinResponse> result = controller.reenviarPin(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(reenviarPinInputPort).execute(request);
    }
}

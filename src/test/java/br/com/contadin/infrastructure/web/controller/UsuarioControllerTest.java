package br.com.contadin.infrastructure.web.controller;

import br.com.contadin.application.dto.usuario.AtualizarUsuarioRequest;
import br.com.contadin.application.dto.usuario.ListarUsuariosResponse;
import br.com.contadin.application.dto.usuario.UsuarioResponse;
import br.com.contadin.application.port.in.usuario.AtualizarUsuarioInputPort;
import br.com.contadin.application.port.in.usuario.BuscarUsuarioInputPort;
import br.com.contadin.application.port.in.usuario.DesativarUsuarioInputPort;
import br.com.contadin.application.port.in.usuario.ListarUsuariosInputPort;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.infrastructure.web.mapper.UsuarioWebMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock ListarUsuariosInputPort listarUsuariosInputPort;
    @Mock BuscarUsuarioInputPort buscarUsuarioInputPort;
    @Mock AtualizarUsuarioInputPort atualizarUsuarioInputPort;
    @Mock DesativarUsuarioInputPort desativarUsuarioInputPort;
    @Mock UsuarioWebMapper usuarioWebMapper;

    @InjectMocks UsuarioController controller;

    @Test
    void deveListarUsuariosERetornar200() {
        UUID id = UUID.randomUUID();
        Usuario usuario = Usuario.builder().id(id).nome("João").build();
        ListarUsuariosResponse listarResponse = new ListarUsuariosResponse(id, "João");

        when(listarUsuariosInputPort.execute()).thenReturn(List.of(usuario));
        when(usuarioWebMapper.toListarResponse(usuario)).thenReturn(listarResponse);

        ResponseEntity<List<ListarUsuariosResponse>> result = controller.listar();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(List.of(listarResponse), result.getBody());
        verify(listarUsuariosInputPort).execute();
    }

    @Test
    void deveBuscarUsuarioPorIdERetornar200() {
        UUID id = UUID.randomUUID();
        Usuario usuario = Usuario.builder().id(id).nome("Maria").build();
        UsuarioResponse response = new UsuarioResponse(id, "Maria", "Silva", "maria@email.com", "11999999999", true, null, null);

        when(buscarUsuarioInputPort.execute(id)).thenReturn(usuario);
        when(usuarioWebMapper.toUsuarioResponse(usuario)).thenReturn(response);

        ResponseEntity<UsuarioResponse> result = controller.buscarPorId(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(buscarUsuarioInputPort).execute(id);
    }

    @Test
    void deveAtualizarUsuarioERetornar200() {
        UUID id = UUID.randomUUID();
        AtualizarUsuarioRequest request = new AtualizarUsuarioRequest("João", "Santos", "11988888888");
        Usuario patch = Usuario.builder().nome("João").build();
        Usuario atualizado = Usuario.builder().id(id).nome("João").build();
        UsuarioResponse response = new UsuarioResponse(id, "João", "Santos", "joao@email.com", "11988888888", true, null, null);

        when(usuarioWebMapper.toDomain(request)).thenReturn(patch);
        when(atualizarUsuarioInputPort.execute(id, patch)).thenReturn(atualizado);
        when(usuarioWebMapper.toUsuarioResponse(atualizado)).thenReturn(response);

        ResponseEntity<UsuarioResponse> result = controller.atualizar(id, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(atualizarUsuarioInputPort).execute(id, patch);
    }

    @Test
    void deveDesativarUsuarioERetornar204() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> result = controller.desativarUsuario(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(desativarUsuarioInputPort).execute(id);
    }
}

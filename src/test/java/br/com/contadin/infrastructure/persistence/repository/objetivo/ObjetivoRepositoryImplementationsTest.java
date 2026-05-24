package br.com.contadin.infrastructure.persistence.repository.objetivo;

import br.com.contadin.domain.model.Objetivo;
import br.com.contadin.infrastructure.persistence.entity.ObjetivoEntity;
import br.com.contadin.infrastructure.persistence.mapper.ObjetivoPersistenceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObjetivoRepositoryImplementationsTest {

    @Mock
    ObjetivoPersistenceMapper mapper;

    @Mock
    ObjetivoJpaRepository jpaRepository;

    @InjectMocks
    ObjetivoRepositoryImplementations repo;

    @Test
    void deveSalvarObjetivo() {
        ObjetivoEntity entity = mock(ObjetivoEntity.class);
        ObjetivoEntity saved = mock(ObjetivoEntity.class);
        Objetivo domain = mock(Objetivo.class);
        Objetivo savedDomain = mock(Objetivo.class);
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(savedDomain);

        Objetivo result = repo.save(domain);

        assertEquals(savedDomain, result);
        verify(mapper).toEntity(domain);
        verify(jpaRepository).save(entity);
        verify(mapper).toDomain(saved);
    }

    @Test
    void deveBuscarPorIdQuandoEncontrado() {
        UUID id = UUID.randomUUID();
        ObjetivoEntity entity = mock(ObjetivoEntity.class);
        Objetivo domain = mock(Objetivo.class);
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Objetivo> result = repo.findById(id);

        assertTrue(result.isPresent());
        assertEquals(domain, result.get());
        verify(jpaRepository).findById(id);
    }

    @Test
    void deveRetornarVazioQuandoObjetivoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Objetivo> result = repo.findById(id);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findById(id);
    }

    @Test
    void deveBuscarObjetivosPorUsuario() {
        UUID fkUsuario = UUID.randomUUID();
        ObjetivoEntity entity = mock(ObjetivoEntity.class);
        Objetivo domain = mock(Objetivo.class);
        when(jpaRepository.findByFkUsuario(fkUsuario)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Objetivo> result = repo.findByUsuario(fkUsuario);

        assertEquals(List.of(domain), result);
        verify(jpaRepository).findByFkUsuario(fkUsuario);
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioSemObjetivos() {
        UUID fkUsuario = UUID.randomUUID();
        when(jpaRepository.findByFkUsuario(fkUsuario)).thenReturn(List.of());

        List<Objetivo> result = repo.findByUsuario(fkUsuario);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findByFkUsuario(fkUsuario);
    }

    @Test
    void deveBuscarObjetivosPorNomeEUsuario() {
        String nome = "viagem";
        UUID fkUsuario = UUID.randomUUID();
        ObjetivoEntity entity = mock(ObjetivoEntity.class);
        Objetivo domain = mock(Objetivo.class);
        when(jpaRepository.findByFkUsuarioAndNomeContainingIgnoreCase(fkUsuario, nome))
                .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Objetivo> result = repo.findByNomeAndUsuario(nome, fkUsuario);

        assertEquals(List.of(domain), result);
        verify(jpaRepository).findByFkUsuarioAndNomeContainingIgnoreCase(fkUsuario, nome);
    }

    @Test
    void deveRetornarListaVaziaQuandoObjetivoNaoEncontradoPorNome() {
        String nome = "inexistente";
        UUID fkUsuario = UUID.randomUUID();
        when(jpaRepository.findByFkUsuarioAndNomeContainingIgnoreCase(fkUsuario, nome))
                .thenReturn(List.of());

        List<Objetivo> result = repo.findByNomeAndUsuario(nome, fkUsuario);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findByFkUsuarioAndNomeContainingIgnoreCase(fkUsuario, nome);
    }

    @Test
    void deveDeletarPorId() {
        UUID id = UUID.randomUUID();

        repo.deleteById(id);

        verify(jpaRepository).deleteById(id);
    }
}

package br.com.contadin.infrastructure.persistence.repository.categoria;

import br.com.contadin.domain.enums.TipoCategoria;
import br.com.contadin.domain.model.Categoria;
import br.com.contadin.infrastructure.persistence.entity.CategoriaEntity;
import br.com.contadin.infrastructure.persistence.mapper.CategoriaPersistenceMapper;
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
class CategoriaRepositoryImplementationsTest {

    @Mock
    CategoriaJpaRepository jpaRepository;

    @Mock
    CategoriaPersistenceMapper mapper;

    @InjectMocks
    CategoriaRepositoryImplementations repo;

    @Test
    void deveSalvarCategoria() {
        CategoriaEntity entity = mock(CategoriaEntity.class);
        CategoriaEntity saved = mock(CategoriaEntity.class);
        Categoria domain = mock(Categoria.class);
        Categoria savedDomain = mock(Categoria.class);
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(savedDomain);

        Categoria result = repo.save(domain);

        assertEquals(savedDomain, result);
        verify(jpaRepository).save(entity);
        verify(mapper).toDomain(saved);
    }

    @Test
    void deveBuscarPorIdAtivoQuandoEncontrado() {
        UUID id = UUID.randomUUID();
        CategoriaEntity entity = mock(CategoriaEntity.class);
        Categoria domain = mock(Categoria.class);
        when(jpaRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Categoria> result = repo.findById(id);

        assertTrue(result.isPresent());
        assertEquals(domain, result.get());
        verify(jpaRepository).findByIdAndAtivoTrue(id);
    }

    @Test
    void deveRetornarVazioQuandoNaoEncontradoPorId() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.findByIdAndAtivoTrue(id)).thenReturn(Optional.empty());

        Optional<Categoria> result = repo.findById(id);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findByIdAndAtivoTrue(id);
    }

    @Test
    void deveBuscarPorIdIncluindoInativaQuandoEncontrado() {
        UUID id = UUID.randomUUID();
        CategoriaEntity entity = mock(CategoriaEntity.class);
        Categoria domain = mock(Categoria.class);
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Categoria> result = repo.findByIdIncludingInactive(id);

        assertTrue(result.isPresent());
        assertEquals(domain, result.get());
        verify(jpaRepository).findById(id);
    }

    @Test
    void deveRetornarVazioQuandoInativaaNaoEncontrada() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Categoria> result = repo.findByIdIncludingInactive(id);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findById(id);
    }

    @Test
    void deveDeletarPorId() {
        UUID id = UUID.randomUUID();

        repo.deleteById(id);

        verify(jpaRepository).deleteById(id);
    }

    @Test
    void deveBuscarTodasAtivasPorUsuario() {
        UUID fkUsuario = UUID.randomUUID();
        CategoriaEntity entity = mock(CategoriaEntity.class);
        Categoria domain = mock(Categoria.class);
        when(jpaRepository.findByFkUsuarioAndAtivoTrue(fkUsuario)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Categoria> result = repo.findTodasByUsuario(fkUsuario);

        assertEquals(List.of(domain), result);
        verify(jpaRepository).findByFkUsuarioAndAtivoTrue(fkUsuario);
    }

    @Test
    void deveBuscarPorUsuarioETipo() {
        UUID fkUsuario = UUID.randomUUID();
        TipoCategoria tipo = TipoCategoria.GASTO;
        CategoriaEntity entity = mock(CategoriaEntity.class);
        Categoria domain = mock(Categoria.class);
        when(jpaRepository.findByFkUsuarioAndTipoIncludingGlobalAndAtivoTrue(fkUsuario, tipo))
                .thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Categoria> result = repo.findByUsuario(fkUsuario, tipo);

        assertEquals(List.of(domain), result);
        verify(jpaRepository).findByFkUsuarioAndTipoIncludingGlobalAndAtivoTrue(fkUsuario, tipo);
    }

    @Test
    void deveBuscarInativasPorUsuario() {
        UUID fkUsuario = UUID.randomUUID();
        CategoriaEntity entity = mock(CategoriaEntity.class);
        Categoria domain = mock(Categoria.class);
        when(jpaRepository.findByFkUsuarioAndAtivoFalse(fkUsuario)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Categoria> result = repo.findByUsuarioInativas(fkUsuario);

        assertEquals(List.of(domain), result);
        verify(jpaRepository).findByFkUsuarioAndAtivoFalse(fkUsuario);
    }

    @Test
    void deveBuscarPorNomeEUsuario() {
        String nome = "alimentação";
        UUID fkUsuario = UUID.randomUUID();
        TipoCategoria tipo = TipoCategoria.GASTO;
        CategoriaEntity entity = mock(CategoriaEntity.class);
        Categoria domain = mock(Categoria.class);
        when(jpaRepository.findByFkUsuarioAndNomeContainingIgnoreCaseAndTipoIncludingGlobalAndAtivoTrue(
                fkUsuario, nome, tipo)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Categoria> result = repo.findByNomeAndUsuario(nome, fkUsuario, tipo);

        assertEquals(List.of(domain), result);
        verify(jpaRepository)
                .findByFkUsuarioAndNomeContainingIgnoreCaseAndTipoIncludingGlobalAndAtivoTrue(fkUsuario, nome, tipo);
    }

    @Test
    void deveRetornarListaVaziaQuandoNenhumaCategoriaEncontrada() {
        UUID fkUsuario = UUID.randomUUID();
        when(jpaRepository.findByFkUsuarioAndAtivoTrue(fkUsuario)).thenReturn(List.of());

        List<Categoria> result = repo.findTodasByUsuario(fkUsuario);

        assertTrue(result.isEmpty());
    }
}

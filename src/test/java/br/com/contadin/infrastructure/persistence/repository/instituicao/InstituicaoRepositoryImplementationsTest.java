package br.com.contadin.infrastructure.persistence.repository.instituicao;

import br.com.contadin.domain.model.Instituicao;
import br.com.contadin.infrastructure.persistence.entity.InstituicaoEntity;
import br.com.contadin.infrastructure.persistence.mapper.InstituicaoPersistenceMapper;
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
class InstituicaoRepositoryImplementationsTest {

    @Mock
    InstituicaoPersistenceMapper mapper;

    @Mock
    InstituicaoJpaRepository jpaRepository;

    @InjectMocks
    InstituicaoRepositoryImplementations repo;

    @Test
    void deveSalvarInstituicao() {
        InstituicaoEntity entity = mock(InstituicaoEntity.class);
        Instituicao domain = mock(Instituicao.class);
        Instituicao savedDomain = mock(Instituicao.class);
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(savedDomain);

        Instituicao result = repo.save(domain);

        assertEquals(savedDomain, result);
        verify(mapper).toEntity(domain);
        verify(jpaRepository).save(entity);
        verify(mapper).toDomain(entity);
    }

    @Test
    void deveBuscarPorIdQuandoEncontrado() {
        UUID id = UUID.randomUUID();
        InstituicaoEntity entity = mock(InstituicaoEntity.class);
        Instituicao domain = mock(Instituicao.class);
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Instituicao> result = repo.findById(id);

        assertTrue(result.isPresent());
        assertEquals(domain, result.get());
        verify(jpaRepository).findById(id);
    }

    @Test
    void deveRetornarVazioQuandoInstituicaoNaoEncontrada() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Instituicao> result = repo.findById(id);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findById(id);
    }

    @Test
    void deveBuscarAtivasPorUsuario() {
        UUID fkUsuario = UUID.randomUUID();
        InstituicaoEntity entity = mock(InstituicaoEntity.class);
        Instituicao domain = mock(Instituicao.class);
        when(jpaRepository.findByFkUsuarioAndAtivoTrue(fkUsuario)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Instituicao> result = repo.findAtivasByUsuario(fkUsuario);

        assertEquals(List.of(domain), result);
        verify(jpaRepository).findByFkUsuarioAndAtivoTrue(fkUsuario);
    }

    @Test
    void deveRetornarListaVaziaQuandoNenhumaInstituicaoAtiva() {
        UUID fkUsuario = UUID.randomUUID();
        when(jpaRepository.findByFkUsuarioAndAtivoTrue(fkUsuario)).thenReturn(List.of());

        List<Instituicao> result = repo.findAtivasByUsuario(fkUsuario);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findByFkUsuarioAndAtivoTrue(fkUsuario);
    }

    @Test
    void deveBuscarPorNomeEUsuario() {
        UUID fkUsuario = UUID.randomUUID();
        String nome = "Nubank";
        InstituicaoEntity entity = mock(InstituicaoEntity.class);
        Instituicao domain = mock(Instituicao.class);
        when(jpaRepository.findByFkUsuarioAndNome(fkUsuario, nome)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Instituicao> result = repo.findByNomeAndUsuario(fkUsuario, nome);

        assertEquals(List.of(domain), result);
        verify(jpaRepository).findByFkUsuarioAndNome(fkUsuario, nome);
    }

    @Test
    void deveRetornarListaVaziaQuandoInstituicaoNaoEncontradaPorNome() {
        UUID fkUsuario = UUID.randomUUID();
        String nome = "Inexistente";
        when(jpaRepository.findByFkUsuarioAndNome(fkUsuario, nome)).thenReturn(List.of());

        List<Instituicao> result = repo.findByNomeAndUsuario(fkUsuario, nome);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findByFkUsuarioAndNome(fkUsuario, nome);
    }
}

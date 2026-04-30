package br.com.contadin.infrastructure.persistence.repository.instituicao;

import br.com.contadin.infrastructure.persistence.entity.InstituicaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InstituicaoJpaRepository extends JpaRepository<InstituicaoEntity, UUID> {
    List<InstituicaoEntity> findByFkUsuarioAndAtivoTrue(UUID fkUsuario);
    List<InstituicaoEntity> findByFkUsuarioAndNome(UUID fkUsuario, String nome);
}

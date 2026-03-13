package br.com.contadin.infrastructure.persistence.repository.instituicao;

import br.com.contadin.infrastructure.persistence.entity.InstituicaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstituicaoJpaRepository extends JpaRepository<InstituicaoEntity, Integer> {
    List<InstituicaoEntity> findByFkUsuarioAndAtivoTrue(Integer fkUsuario);
    List<InstituicaoEntity> findByFkUsuarioAndNome(Integer fkUsuario, String nome);
}

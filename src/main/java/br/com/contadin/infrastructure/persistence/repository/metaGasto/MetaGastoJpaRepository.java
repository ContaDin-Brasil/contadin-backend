package br.com.contadin.infrastructure.persistence.repository.metaGasto;

import br.com.contadin.infrastructure.persistence.entity.MetaGastoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MetaGastoJpaRepository extends JpaRepository< MetaGastoEntity, Integer> {
    List<MetaGastoEntity> findByFkUsuario(Integer fkUsuario);
    List<MetaGastoEntity> findByFkUsuarioAndNome(Integer fkUsuario, String nome);
}

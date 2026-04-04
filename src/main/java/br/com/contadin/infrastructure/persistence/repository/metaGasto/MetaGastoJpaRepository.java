package br.com.contadin.infrastructure.persistence.repository.metaGasto;

import br.com.contadin.infrastructure.persistence.entity.MetaGastoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface MetaGastoJpaRepository extends JpaRepository< MetaGastoEntity, UUID> {
    List<MetaGastoEntity> findByFkUsuario(Integer fkUsuario);
    List<MetaGastoEntity> findByFkUsuarioAndNome(Integer fkUsuario, String nome);
}

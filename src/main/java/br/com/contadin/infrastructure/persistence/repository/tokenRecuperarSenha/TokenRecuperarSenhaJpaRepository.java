package br.com.contadin.infrastructure.persistence.repository.tokenRecuperarSenha;

import br.com.contadin.infrastructure.persistence.entity.TokenRecuperarSenhaEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRecuperarSenhaJpaRepository extends JpaRepository<TokenRecuperarSenhaEntity, UUID> {

    Optional<TokenRecuperarSenhaEntity> findFirstByFkUsuarioAndUtilizadoFalseOrderByCriadoEmDesc(UUID fkUsuario);

    List<TokenRecuperarSenhaEntity> findByFkUsuarioOrderByCriadoEmDesc(UUID fkUsuario);

    @Modifying
    @Transactional
    @Query("""
            UPDATE TokenRecuperarSenhaEntity t
            SET t.utilizado = true
            WHERE t.fkUsuario = :fkUsuario
            AND (t.utilizado = false OR t.utilizado IS NULL)
            """)
    void invalidarTokensDoUsuario(@Param("fkUsuario") UUID fkUsuario);

    @Modifying
    @Transactional
    @Query("""
            UPDATE TokenRecuperarSenhaEntity t
            SET t.utilizado = true
            WHERE t.id = :id
            """)
    void marcarComoUtilizado(@Param("id") UUID id);
}

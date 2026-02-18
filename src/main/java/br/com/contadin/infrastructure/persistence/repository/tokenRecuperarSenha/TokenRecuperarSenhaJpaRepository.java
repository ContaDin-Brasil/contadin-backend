package br.com.contadin.infrastructure.persistence.repository.tokenRecuperarSenha;

import br.com.contadin.infrastructure.persistence.entity.TokenRecuperarSenhaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRecuperarSenhaJpaRepository extends JpaRepository<TokenRecuperarSenhaEntity, Integer> {
}

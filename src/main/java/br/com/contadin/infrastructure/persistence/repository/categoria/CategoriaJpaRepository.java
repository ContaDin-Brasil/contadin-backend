package br.com.contadin.infrastructure.persistence.repository.categoria;

import br.com.contadin.domain.enums.TipoCategoria;
import br.com.contadin.infrastructure.persistence.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaJpaRepository extends JpaRepository<CategoriaEntity, UUID> {
	Optional<CategoriaEntity> findByIdAndAtivoTrue(UUID id);

	List<CategoriaEntity> findByFkUsuarioAndAtivoTrue(UUID fkUsuario);

	@Query("""
		SELECT c
		FROM CategoriaEntity c
		WHERE c.fkUsuario = :fkUsuario
		  AND c.ativo = true
		  AND (c.tipo = :tipoCategoria OR c.tipo = br.com.contadin.domain.enums.TipoCategoria.GLOBAL)
		""")
	List<CategoriaEntity> findByFkUsuarioAndTipoIncludingGlobalAndAtivoTrue(@Param("fkUsuario") UUID fkUsuario,
			@Param("tipoCategoria") TipoCategoria tipoCategoria);

	List<CategoriaEntity> findByFkUsuarioAndAtivoFalse(UUID fkUsuario);

	@Query("""
		SELECT c
		FROM CategoriaEntity c
		WHERE c.fkUsuario = :fkUsuario
		  AND c.ativo = true
		  AND LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
		  AND (c.tipo = :tipoCategoria OR c.tipo = br.com.contadin.domain.enums.TipoCategoria.GLOBAL)
		""")
	List<CategoriaEntity> findByFkUsuarioAndNomeContainingIgnoreCaseAndTipoIncludingGlobalAndAtivoTrue(
			@Param("fkUsuario") UUID fkUsuario,
			@Param("nome") String nome,
			@Param("tipoCategoria") TipoCategoria tipoCategoria);
}

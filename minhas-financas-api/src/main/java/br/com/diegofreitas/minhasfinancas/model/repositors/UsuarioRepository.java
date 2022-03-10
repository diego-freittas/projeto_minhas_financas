package br.com.diegofreitas.minhasfinancas.model.repositors;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.diegofreitas.minhasfinancas.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
	boolean existsByEmail(String email);
	
	Optional<Usuario> findByEmail(String email);
	
}

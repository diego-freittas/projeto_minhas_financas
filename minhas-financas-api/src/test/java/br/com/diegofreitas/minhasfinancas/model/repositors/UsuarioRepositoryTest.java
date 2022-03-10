package br.com.diegofreitas.minhasfinancas.model.repositors;

import java.util.Optional;

import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.diegofreitas.minhasfinancas.model.entity.Usuario;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository usuarioRepository;

	@Autowired
	TestEntityManager entityManager;

//	@Before
//	public void setUP() {
//		usuario = Usuario.builder().nome("Usuario").email("usuario@email.com").build();
//		repository.save(usuario);
//	}

	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
		// cenario

		// acao
		boolean result = usuarioRepository.existsByEmail("usuario@email.com");

		// verificacao
		Assertions.assertThat(result).isFalse();

	}

	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		// cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);

		// verificacao
		Optional<Usuario> result = usuarioRepository.findByEmail("usuario@email.com");
		Assertions.assertThat(result.isPresent()).isTrue();
	}

	@Test
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase() {
		// verificacao
		Optional<Usuario> result = usuarioRepository.findByEmail("usuario@email.com");
		Assertions.assertThat(result.isPresent()).isFalse();
	}

	public static Usuario criarUsuario() {
		return Usuario.builder().nome("Usuario").email("usuario@email.com").senha("senha").build();
	}


}

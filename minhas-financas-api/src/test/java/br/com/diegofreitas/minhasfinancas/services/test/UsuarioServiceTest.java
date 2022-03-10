package br.com.diegofreitas.minhasfinancas.services.test;


import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.diegofreitas.minhasfinancas.exception.ErroAutenticacao;
import br.com.diegofreitas.minhasfinancas.exception.RegraNegocioException;
import br.com.diegofreitas.minhasfinancas.model.entity.Usuario;
import br.com.diegofreitas.minhasfinancas.model.repositors.UsuarioRepository;
import br.com.diegofreitas.minhasfinancas.services.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {


	@SpyBean
	UsuarioServiceImpl usuarioService;
	
	@MockBean
	UsuarioRepository usuarioRepository;
	
	@Test
	public void deveSalvarUmUsuario() {
		//cenario
		Mockito.doNothing().when(usuarioService).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder()
								.id(1L)
								.nome("nome")
								.email("email@email.com")
								.senha("senha")
								.build();
		
		Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//acao
		Usuario usuarioSalvo = usuarioService.salvarUsuario(new Usuario());
		
		//verificacao
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		//cenario
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(usuarioService).validarEmail(email);
		//acao
		org.junit.jupiter.api.Assertions
		.assertThrows(RegraNegocioException.class, ()-> usuarioService.salvarUsuario(usuario));
		//validacao
		Mockito.verify(usuarioRepository,Mockito.never()).save(usuario);	
	}
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		//cenario
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1L).build();
		Mockito.when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		//acao
		Usuario result = usuarioService.autenticar(email, senha);
		
		//validacao
		Assertions.assertThat(result).isNotNull();
	}
	
	@Test
	public void deveLancarErroQUandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		
		//cenário
		Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//acao
		Throwable exception = Assertions.catchThrowable( () -> usuarioService.autenticar("email@email.com", "senha") );
		
		//verificacao
		Assertions.assertThat(exception)
			.isInstanceOf(ErroAutenticacao.class)
			.hasMessage("Usuário não encontrado para o email informado.");
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		//cenario
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		//acao
		Throwable exception = Assertions.catchThrowable( () ->  usuarioService.autenticar("email@email.com", "123") );
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida.");
		
	}
	
	@Test
	public void deveValidarEmail() {
		// cenario
		Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		//acao
		usuarioService.validarEmail("email@email.com");
	}
	
	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		//cenario
		Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//acao
		org.junit.jupiter.api.Assertions
			.assertThrows(RegraNegocioException.class, () -> usuarioService.validarEmail("email@email.com"));
	}
}
	


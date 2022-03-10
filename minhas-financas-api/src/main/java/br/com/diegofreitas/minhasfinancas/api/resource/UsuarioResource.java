package br.com.diegofreitas.minhasfinancas.api.resource;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.diegofreitas.minhasfinancas.api.dto.TokenDTO;
import br.com.diegofreitas.minhasfinancas.api.dto.UsuarioDTO;
import br.com.diegofreitas.minhasfinancas.exception.ErroAutenticacao;
import br.com.diegofreitas.minhasfinancas.exception.RegraNegocioException;
import br.com.diegofreitas.minhasfinancas.model.entity.Usuario;
import br.com.diegofreitas.minhasfinancas.services.JwtService;
import br.com.diegofreitas.minhasfinancas.services.LancamentoService;
import br.com.diegofreitas.minhasfinancas.services.UsuarioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioResource {

	// Como tem a anotação que cria um construtor com argumentos o
	// Spring já faz a injerção de dependencia.
	private final UsuarioService usuarioService;
	private final LancamentoService lancamentoService;
	private final JwtService jwtService;

	@PostMapping("/autenticar")
	public ResponseEntity<?> autenticar(@RequestBody UsuarioDTO usuarioDTO) {
		try {
			Usuario usuarioAutenticado = usuarioService.autenticar(usuarioDTO.getEmail(), usuarioDTO.getSenha());
			String token = jwtService.gerarToken(usuarioAutenticado);
			TokenDTO tokenDTO = new TokenDTO(usuarioAutenticado.getNome(), token);
			return ResponseEntity.ok(usuarioAutenticado);
		} catch (ErroAutenticacao e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity<?> salvar(@RequestBody UsuarioDTO usuarioDto) {

		Usuario usuario = Usuario.builder().nome(usuarioDto.getNome()).senha(usuarioDto.getSenha())
				.email(usuarioDto.getEmail()).build();

		try {
			Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("{id}")
	public ResponseEntity<?> obterSaldo(@PathVariable("id") Long id) {
		Optional<Usuario> usuario = usuarioService.obterPorId(id);
		if (!usuario.isPresent()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
		return ResponseEntity.ok(saldo);
	}
	
	@GetMapping
	public String toaqui() {
		return "to aqui";
	}
	
}
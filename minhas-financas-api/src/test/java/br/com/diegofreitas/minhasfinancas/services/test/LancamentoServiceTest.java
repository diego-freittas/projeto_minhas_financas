package br.com.diegofreitas.minhasfinancas.services.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.diegofreitas.minhasfinancas.exception.RegraNegocioException;
import br.com.diegofreitas.minhasfinancas.model.entity.Lancamento;
import br.com.diegofreitas.minhasfinancas.model.entity.Usuario;
import br.com.diegofreitas.minhasfinancas.model.enuns.StatusLancamento;
import br.com.diegofreitas.minhasfinancas.model.enuns.TipoLancamento;
import br.com.diegofreitas.minhasfinancas.model.repositors.LancamentoRepository;
import br.com.diegofreitas.minhasfinancas.model.repositors.LancamentoRepositoryTest;
import br.com.diegofreitas.minhasfinancas.services.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl lancamentoService;
	@MockBean
	LancamentoRepository lancamentoRepository;
	
	@Test
	public void deveSalvarUmLancamento() {
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		doNothing().when(lancamentoService).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		when(lancamentoRepository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//execucao
		Lancamento lancamento = lancamentoService.salvar(lancamentoASalvar);
		
		//verificação
		assertThat( lancamento.getId() ).isEqualTo(lancamentoSalvo.getId());
		assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		doThrow( RegraNegocioException.class ).when(lancamentoService).validar(lancamentoASalvar);
		
		//execucao e verificacao
		catchThrowableOfType( () -> lancamentoService.salvar(lancamentoASalvar), RegraNegocioException.class );
		verify(lancamentoRepository, never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

		doNothing().when(lancamentoService).validar(lancamentoSalvo);
		
		when(lancamentoRepository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		//execucao
		lancamentoService.atualizar(lancamentoSalvo);
		
		//verificação
		verify(lancamentoRepository, times(1)).save(lancamentoSalvo);
		
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//execucao e verificacao
		catchThrowableOfType( () -> lancamentoService.atualizar(lancamento), NullPointerException.class );
		verify(lancamentoRepository, never()).save(lancamento);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		//execucao
		lancamentoService.deletar(lancamento);
		
		//verificacao
		verify( lancamentoRepository ).delete(lancamento);
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//execucao
		catchThrowableOfType( () -> lancamentoService.deletar(lancamento), NullPointerException.class );
		
		//verificacao
		verify( lancamentoRepository, never() ).delete(lancamento);
	}
	
	
	@Test
	public void deveFiltrarLancamentos() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		when( lancamentoRepository.findAll(any(Example.class)) ).thenReturn(lista);
		
		//execucao
		List<Lancamento> resultado = lancamentoService.buscar(lancamento);
		
		//verificacoes
		assertThat(resultado)
			.isNotEmpty()
			.hasSize(1)
			.contains(lancamento);
		
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		doReturn(lancamento).when(lancamentoService).atualizar(lancamento);
		
		//execucao
		lancamentoService.atualizarStatus(lancamento, novoStatus);
		
		//verificacoes
		assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		verify(lancamentoService).atualizar(lancamento);
		
	}
	
	@Test
	public void deveObterUmLancamentoPorID() {
		//cenário
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		when(lancamentoRepository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//execucao
		Optional<Lancamento> resultado =  lancamentoService.obterPorId(id);
		
		//verificacao
		assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void deveREtornarVazioQuandoOLancamentoNaoExiste() {
		//cenário
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		when( lancamentoRepository.findById(id) ).thenReturn( Optional.empty() );
		
		//execucao
		Optional<Lancamento> resultado =  lancamentoService.obterPorId(id);
		
		//verificacao
		assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
		
		lancamento.setDescricao("");
		
		erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
		
		lancamento.setDescricao("Salario");
		
		erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		lancamento.setAno(0);
		
		erro = catchThrowable( () -> lancamentoService.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		lancamento.setAno(13);
		
		erro = catchThrowable( () -> lancamentoService.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(1);
		
		erro = catchThrowable( () -> lancamentoService.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		lancamento.setAno(202);
		
		erro = catchThrowable( () -> lancamentoService.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		lancamento.setAno(2020);
		
		erro = catchThrowable( () -> lancamentoService.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
		
		lancamento.setUsuario(new Usuario());
		
		erro = catchThrowable( () -> lancamentoService.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
		
		lancamento.getUsuario().setId(1l);
		
		erro = catchThrowable( () -> lancamentoService.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
		
		lancamento.setValor(BigDecimal.ZERO);
		
		erro = catchThrowable( () -> lancamentoService.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
		
		lancamento.setValor(BigDecimal.valueOf(1));
		
		erro = catchThrowable( () -> lancamentoService.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de Lançamento.");
		
	}
	
//	@Test
//	public void deveObterSaldoPorUsuario() {
//		//cenario
//		Long idUsuario = 1l;
//		
//		when( lancamentoRepository
//				.obterSaldoPorTipoLancamentoEUsuarioEStatus(idUsuario, TipoLancamento.RECEITA, StatusLancamento.EFETIVADO)) 
//				.thenReturn(BigDecimal.valueOf(100));
//		
//		when( lancamentoRepository
//				.obterSaldoPorTipoLancamentoEUsuarioEStatus(idUsuario, TipoLancamento.DESPESA, StatusLancamento.EFETIVADO)) 
//				.thenReturn(BigDecimal.valueOf(50));
//		
//		//execucao
//		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(idUsuario);
//		
//		//verificacao
//		assertThat(saldo).isEqualTo(BigDecimal.valueOf(50));
//		
//	}
	
}

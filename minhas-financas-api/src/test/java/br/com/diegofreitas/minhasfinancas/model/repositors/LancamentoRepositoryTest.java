package br.com.diegofreitas.minhasfinancas.model.repositors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.repository.support.Repositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.diegofreitas.minhasfinancas.model.entity.Lancamento;
import br.com.diegofreitas.minhasfinancas.model.enuns.StatusLancamento;
import br.com.diegofreitas.minhasfinancas.model.enuns.TipoLancamento;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository lancamentoRepository;

	@Autowired
	TestEntityManager entityManager;

	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamento = criarLancamento();
		lancamento = lancamentoRepository.save(lancamento);
		assertThat(lancamento.getId()).isNotNull();
	}

	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = criarEPersistirUmLancamento();
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		lancamentoRepository.delete(lancamento);

		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
		assertThat(lancamentoInexistente).isNull();
	}

	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamento = criarEPersistirUmLancamento();

		lancamento.setAno(2021);
		lancamento.setDescricao("Teste Atualizacao");
		lancamento.setStatus(StatusLancamento.CANCELADO);
		;

		lancamentoRepository.save(lancamento);

		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2021);
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste Atualizacao");
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
	}

	@Test
	public void deveBuscarUmLancamentoPorId() {
		Lancamento lancamento = criarEPersistirUmLancamento();
		Optional<Lancamento> lancamentoEncontrado = lancamentoRepository.findById(lancamento.getId());
		assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}

	public static Lancamento criarLancamento() {
		return Lancamento.builder().ano(2019).mes(1).descricao("lancamento qualquer").valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA).status(StatusLancamento.PENDENTE).dataCadastro(LocalDate.now()).build();
	}

	private Lancamento criarEPersistirUmLancamento() {
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return lancamento;
	}
}

package br.com.diegofreitas.minhasfinancas.model.repositors;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.diegofreitas.minhasfinancas.model.entity.Lancamento;
import br.com.diegofreitas.minhasfinancas.model.enuns.StatusLancamento;
import br.com.diegofreitas.minhasfinancas.model.enuns.TipoLancamento;


public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

@Query(value =
		" SELECT SUM(l.valor) FROM Lancamento l join l.usuario u"
	   +" WHERE u.id = :idUsuario AND l.tipo =:tipo AND l.status = :status GROUP BY u")
BigDecimal obterSaldoPorTipoLancamentoEUsuarioEStatus(
		@Param("idUsuario") Long idUsuario,
		@Param("tipo") TipoLancamento tipo,
		@Param("status")StatusLancamento satatus);

}

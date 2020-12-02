package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.comportamento;

import java.util.Iterator;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.cargadedados.CarregaDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.LinhaDeDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.TabelaDeDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.api.ComportamentoOrientadoADados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.api.ComportamentoOrientadoADadosJUnitTestCase;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.spring.scope.EscopoDoMetodoDeTeste;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.utils.NomeTestCaseUtils;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import lombok.Getter;
import lombok.Setter;

public class MetodoComportamentoOrientadoADados extends ComportamentoOrientadoADadosBase
		implements ComportamentoOrientadoADados {
	public MetodoComportamentoOrientadoADados(TestCase testeCase, String nomeDoMetodo, CarregaDados carregaDados) {
		super(testeCase, nomeDoMetodo, carregaDados);
	}

	@Setter
	private Boolean EXECUTOR = false;

	@Getter
	private final TestSuite linhaDeTeste = new TestSuite();

	@Override
	public int countTestCases() {
		TabelaDeDados table = this.carregaDados.carregarTabelas(this.testeCase, this.nomeDoMetodo);
		return table.getQuantidadeDeLinhas();
	}

	@Override
	public void run(TestResult resultado,
			ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase) {
		TabelaDeDados tabela = null;
		LinhaDeDados linha = null;
		TestCase testeCase = null;

		tabela = this.carregaDados.carregarTabelas(this.testeCase, this.nomeDoMetodo);

		for (Iterator<?> iter = tabela.iteratorLinhas(); iter.hasNext();) {
			linha = (LinhaDeDados) iter.next();

			testeCase = criaIteracaoComCasoDeTeste(linha.getId());
			if (EXECUTOR) {
				linhaDeTeste.addTest(testeCase);
			} else {
				testeCase.run(resultado);
			}
		}

	}

	protected TestCase criaIteracaoComCasoDeTeste(String idLinha) {
		return (TestCase) TestSuite.createTest(this.testeCase.getClass(),
				NomeTestCaseUtils.MontarNome(this.nomeDoMetodo, idLinha));
	}

	@Override
	public void setUp(ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase)
			throws Exception {
		comportamentoOrientadoADadosJUnitTestCase.setUpMethod();
	}

	@Override
	public void tearDown(ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase)
			throws Exception {
		comportamentoOrientadoADadosJUnitTestCase.tearDownMethod();
	}

	@Override
	public void endTest(ComportamentoOrientadoADadosJUnitTestCase testCase) {
		EscopoDoMetodoDeTeste.terminaMetodoDoTeste(testCase.getName());
	}

	@Override
	public void startTest(ComportamentoOrientadoADadosJUnitTestCase testCase) {
		EscopoDoMetodoDeTeste.iniciaMetodoDeTeste(testCase.getName());
	}
}
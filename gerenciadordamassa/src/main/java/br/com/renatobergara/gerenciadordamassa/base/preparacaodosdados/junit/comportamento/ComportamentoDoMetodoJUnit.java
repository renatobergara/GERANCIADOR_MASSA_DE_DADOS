package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.comportamento;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.api.ComportamentoOrientadoADados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.api.ComportamentoOrientadoADadosJUnitTestCase;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.spring.scope.EscopoDoMetodoDeTeste;
import junit.framework.TestResult;

public class ComportamentoDoMetodoJUnit implements ComportamentoOrientadoADados {
	@Override
	public int countTestCases() {
		return 1;
	}

	public void run(TestResult resultado,
			ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase) {
		comportamentoOrientadoADadosJUnitTestCase.superRun(resultado);
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

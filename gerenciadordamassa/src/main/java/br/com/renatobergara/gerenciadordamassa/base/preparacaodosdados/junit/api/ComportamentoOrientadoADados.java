package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.api;

import junit.framework.TestResult;

public interface ComportamentoOrientadoADados {
	public int countTestCases();

	public void startTest(ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase);

	public void setUp(ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase)
			throws Exception;

	public void run(TestResult testResult,
			ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase);

	public void tearDown(ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase)
			throws Exception;

	public void endTest(ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase);
}

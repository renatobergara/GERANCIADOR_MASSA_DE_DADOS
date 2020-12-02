package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.api;

import junit.framework.TestResult;

public abstract interface ComportamentoOrientadoADados {
	public abstract int countTestCases();

	public abstract void startTest(ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase);

	public abstract void setUp(ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase)
			throws Exception;

	public abstract void run(TestResult testResult,
			ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase);

	public abstract void tearDown(ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase)
			throws Exception;

	public abstract void endTest(ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase);
}

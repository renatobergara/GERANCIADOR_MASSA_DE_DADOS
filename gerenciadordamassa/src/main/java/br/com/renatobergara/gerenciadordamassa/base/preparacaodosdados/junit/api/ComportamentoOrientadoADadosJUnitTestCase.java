package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.api;


import java.beans.PropertyEditor;

import junit.framework.TestResult;

public abstract interface ComportamentoOrientadoADadosJUnitTestCase {
	public abstract String getName();

	public abstract void setUpAfterData() throws Exception;

	public abstract void setUpBeforeData() throws Exception;

	public abstract void setUpMethod() throws Exception;

	public abstract void tearDownBeforeData() throws Exception;

	public abstract void tearDownAfterData() throws Exception;

	public abstract void tearDownMethod() throws Exception;

	public abstract PropertyEditor[] getCustomPropertyEditors();

	public abstract void superSetUp() throws Exception;

	public abstract void superTearDown() throws Exception;

	public abstract void superRun(TestResult paramTestResult);
}
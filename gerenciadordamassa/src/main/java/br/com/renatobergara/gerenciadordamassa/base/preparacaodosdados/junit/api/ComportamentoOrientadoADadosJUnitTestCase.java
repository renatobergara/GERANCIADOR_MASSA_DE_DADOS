package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.api;

import java.beans.PropertyEditor;

import junit.framework.TestResult;

public interface ComportamentoOrientadoADadosJUnitTestCase {
	public String getName();

	public void setUpAfterData() throws Exception;

	public void setUpBeforeData() throws Exception;

	public void setUpMethod() throws Exception;

	public void tearDownBeforeData() throws Exception;

	public void tearDownAfterData() throws Exception;

	public void tearDownMethod() throws Exception;

	public PropertyEditor[] getCustomPropertyEditors();

	public void superSetUp() throws Exception;

	public void superTearDown() throws Exception;

	public void superRun(TestResult paramTestResult);
}
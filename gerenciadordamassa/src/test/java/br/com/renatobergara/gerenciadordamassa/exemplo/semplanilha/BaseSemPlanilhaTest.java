package br.com.renatobergara.gerenciadordamassa.exemplo.semplanilha;

import br.com.renatobergara.gerenciadordamassa.base.GerenciadorBaseTestCase;

/**
 * Para teste sem planilha o GerenciadorBaseTestCase deverá ser obrigatório.
 *
 */
public class BaseSemPlanilhaTest extends GerenciadorBaseTestCase {

	public void testSum() {
		int a = 5;
		int b = 10;
		int result = a + b;
		assertEquals(15, result);
	}

}

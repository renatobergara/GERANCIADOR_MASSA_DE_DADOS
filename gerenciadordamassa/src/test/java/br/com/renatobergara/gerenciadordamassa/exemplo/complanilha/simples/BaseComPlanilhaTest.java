package br.com.renatobergara.gerenciadordamassa.exemplo.complanilha.simples;

import org.junit.Test;

import br.com.renatobergara.gerenciadordamassa.base.GerenciadorExcelTestCase;
import br.com.renatobergara.gerenciadordamassa.base.annotation.InjetaAutomatico;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Nome da Classe de teste deverá ser o mesmo nome da planilha. ex.:
 * BaseComPlanilhaTest.java BaseComPlanilhaTest.xls
 * 
 * Para teste com planilha o GerenciadorExcelTestCase deverá ser obrigatório.
 * Nome do método de teste tem que ser o mesmo na aba da planilha.
 *
 */
@Data
@InjetaAutomatico
@EqualsAndHashCode(callSuper = false)
public class BaseComPlanilhaTest extends GerenciadorExcelTestCase {

	private Repositorio modeloDeCadastro;

	@Test
	public void testSum() {
		int a = Integer.parseInt(modeloDeCadastro.getValor1());
		int b = Integer.parseInt(modeloDeCadastro.getValor2());
		int result = a + b;
		assertEquals(modeloDeCadastro.getResultado(), "" + result);
	}
}

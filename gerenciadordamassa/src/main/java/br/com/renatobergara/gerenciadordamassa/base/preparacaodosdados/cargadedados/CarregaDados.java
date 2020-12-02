package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.cargadedados;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.LinhaDeDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.TabelaDeDados;
import junit.framework.TestCase;

public abstract interface CarregaDados {
	public abstract TabelaDeDados carregarTabelas(TestCase paramTestCase, String paramString);

	public abstract LinhaDeDados carregarLinhas(TestCase testCase, String parametro1, String parametro2);
}

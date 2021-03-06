package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.cargadedados;

import org.apache.commons.lang3.Validate;

import br.com.renatobergara.gerenciadordamassa.base.exception.DadosExcelException;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.CarregaElementos;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.Elementos;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.LinhaDeDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.TabelaDeDados;
import junit.framework.TestCase;

public class CarregaDadosImpl implements CarregaDados {
	protected final CarregaElementos carregaElemento;

	public CarregaDadosImpl(CarregaElementos carregaElemento) {
		Validate.notNull(carregaElemento, "O argumento carregaElemento não deve ser nulo.");

		this.carregaElemento = carregaElemento;
	}

	public TabelaDeDados carregarTabelas(TestCase testCase, String NomeDoMetodo) {
		Elementos elementos = null;
		TabelaDeDados dadosDaTabela = null;

		try {
			elementos = this.carregaElemento.carregaElementos(testCase.getClass());
			if (elementos == null) {
				return null;
			}

			dadosDaTabela = elementos.getTabelas(NomeDoMetodo);
		} catch (DadosExcelException e) {
			e.printStackTrace();
		}

		return dadosDaTabela;
	}

	public LinhaDeDados carregarLinhas(TestCase testCase, String methodName, String rowId) {
		TabelaDeDados table = carregarTabelas(testCase, methodName);

		if (table == null) {
			return null;
		}
		return table.EncontraLinhaPorId(rowId);
	}

	public CarregaElementos getCarregaElementos() {
		return this.carregaElemento;
	}

}

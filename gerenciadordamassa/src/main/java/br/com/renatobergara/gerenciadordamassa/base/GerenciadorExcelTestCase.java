package br.com.renatobergara.gerenciadordamassa.base;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.GerenciamentoCachingDataSetLoader;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.cargadedados.CarregaDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.cargadedados.CarregaDadosImpl;

/**
 * Classe que deve ser estendida para implementar testes baseados em planilhas
 * excel.
 */
public class GerenciadorExcelTestCase extends GerenciadorBaseTestCase {

	protected GerenciadorExcelTestCase() {
		super();
	}

	protected GerenciadorExcelTestCase(String nome) {
		super(nome);
	}

	@Override
	public CarregaDados criarCarregadorDeDados() {
		return new CarregaDadosImpl(GerenciamentoCachingDataSetLoader.of());
	}

}
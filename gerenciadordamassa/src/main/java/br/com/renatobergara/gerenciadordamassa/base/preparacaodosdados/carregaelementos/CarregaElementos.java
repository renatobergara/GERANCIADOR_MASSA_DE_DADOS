package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos;

import br.com.renatobergara.gerenciadordamassa.base.exception.DadosExcelException;

public interface CarregaElementos {
	public Elementos carregaElementos(Class<?> parametro) throws DadosExcelException;
}

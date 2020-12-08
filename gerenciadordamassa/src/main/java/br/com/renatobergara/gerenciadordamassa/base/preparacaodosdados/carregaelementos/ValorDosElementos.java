package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos;

import br.com.renatobergara.gerenciadordamassa.base.exception.DadosExcelException;

public interface ValorDosElementos {
	public String getNome();

	public Object getValor() throws DadosExcelException;
}

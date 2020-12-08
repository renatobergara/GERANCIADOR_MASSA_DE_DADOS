package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos;

import java.util.Iterator;

public interface TabelaDeDados {
	public abstract int getQuantidadeDeLinhas();

	public abstract Iterator<LinhaDeDados> iteratorLinhas();

	public abstract String getNome();

	public abstract LinhaDeDados EncontraLinhaPorId(String parametro);
}

package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos;

import java.util.Iterator;

public interface LinhaDeDados {
	public String getId();

	public Iterator<?> iterator();
}

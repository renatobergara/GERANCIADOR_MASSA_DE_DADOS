package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos;

import java.util.Iterator;

public abstract interface LinhaDeDados {
	public abstract String getId();

	public abstract Iterator<?> iterator();
}

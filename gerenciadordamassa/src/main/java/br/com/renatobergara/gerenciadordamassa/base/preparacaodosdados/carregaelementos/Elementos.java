package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos;

import java.io.Serializable;
import java.util.Iterator;

public abstract interface Elementos extends Serializable {
	public abstract String[] getNomesTabelas();

	public abstract Iterator<?> iteratorTabelas();

	public abstract TabelaDeDados getTabelas(String parametro);
}

package br.com.renatobergara.gerenciadordamassa.base.conversor;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class ConversorPlanilhaList implements ConversorPlanilha<List> {

	@Override
	public Class<List> getTipoClass() {
		return List.class;
	}

	@Override
	public List converteValor(Class<? extends List> clazz, Object valor) {
		if (valor.toString().contains(";")) {
			List<String> lista = new ArrayList<String>();
			String[] strings = valor.toString().split(";");
			lista.addAll(Lists.newArrayList(strings));
			return lista;
		}
		return Lists.newArrayList(valor.toString());
	}

}

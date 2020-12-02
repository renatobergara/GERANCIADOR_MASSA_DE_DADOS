package br.com.renatobergara.gerenciadordamassa.base.conversor;

public class ConversorPlanilhaTexto implements ConversorPlanilha<String> {

	@Override
	public String converteValor(Class clazz, Object valor) {
		return valor.toString();
	}

	@Override
	public Class<String> getTipoClass() {
		return String.class;
	}
}

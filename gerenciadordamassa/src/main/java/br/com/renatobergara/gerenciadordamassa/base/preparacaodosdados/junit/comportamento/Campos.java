package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.comportamento;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanWrapper;

import br.com.renatobergara.gerenciadordamassa.base.conversor.ConversorPlanilhaRepositorio;

/**
 * Mantém as regras de criação dos campos dos testes unitários.
 */
final class Campos {

	private final static Pattern REP_PLANILHA_TESTE;
	private final static Pattern REP_PLANILHA_LOCAL;
	private final static Pattern REP_PLANILHA_GLOBAL;
	private final static Pattern ENTIDADE_PERSISTENTE;

	static {
		REP_PLANILHA_TESTE = Pattern.compile("<(/?[^>]+)>");
		REP_PLANILHA_LOCAL = Pattern.compile("<<(/?[^>]+)>>");
		REP_PLANILHA_GLOBAL = Pattern.compile("<<<(/?[^>]+)>>>");
		ENTIDADE_PERSISTENTE = Pattern.compile("\\[(/?[^>]+)\\]");
	}

	private Campos() {
		// Não instancia
	}

	public static Campo of(String coluna, Object valor, BeanWrapper beanWrapper) {

		if (coluna.equals("nomeCenario")) {
			beanWrapper.setPropertyValue("nomeCenario", valor);
		}

		final Matcher mRepPlanilhaTeste = REP_PLANILHA_TESTE.matcher(coluna);
		final Matcher mRepPlanilhaRepLocal = REP_PLANILHA_LOCAL.matcher(coluna);
		final Matcher mRepPlanilhaRepGlobal = REP_PLANILHA_GLOBAL.matcher(coluna);
		final Matcher mEntidade = ENTIDADE_PERSISTENTE.matcher(coluna);
		final Class<?> classeTeste = beanWrapper.getWrappedClass();

		if (mRepPlanilhaRepGlobal.find()) {
			return Campo.of(mRepPlanilhaRepGlobal.group(1), classeTeste, (String) valor,
					new ConversorPlanilhaRepositorio(
							ReflectionUtils.getClassByField(mRepPlanilhaRepGlobal.group(1), classeTeste).getPackage(),
							null));
		} else if (mRepPlanilhaRepLocal.find()) {
			return Campo.of(mRepPlanilhaRepLocal.group(1), classeTeste, (String) valor,
					new ConversorPlanilhaRepositorio(classeTeste.getPackage(), null));
		} else if (mRepPlanilhaTeste.find()) {
			return Campo.of(mRepPlanilhaTeste.group(1), classeTeste, (String) valor,
					new ConversorPlanilhaRepositorio(classeTeste.getPackage(), classeTeste));
		} else if (mEntidade.find()) {
			return Campo.of(mEntidade.group(1), classeTeste, (String) valor);
		}
		return Campo.of(coluna, classeTeste, trataValorNumerico(valor));
	}

	private static Object trataValorNumerico(final Object valor) {
		if (valor instanceof Number) {
			return NumberUtils.createBigDecimal(valor.toString());
		}
		return valor;
	}

}
package br.com.renatobergara.gerenciadordamassa.base.conversor;

import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import br.com.renatobergara.gerenciadordamassa.base.exception.GerenciadorTestException;

public class ConversorUtils {

	private ConversorUtils() {
		// Não pode ser instanciada
	}

	protected final transient Logger logger = Logger.getLogger(this.getClass());

	private static final ConcurrentMap<Class<?>, ConversorPlanilha<?>> tipoConversor = Maps.newConcurrentMap();

	public static void registraConversorTipos(Class<?> chave, ConversorPlanilha<?> conversor) {
		tipoConversor.put(chave, conversor);
	}

	static {
		registraConversorTipos(String.class, new ConversorPlanilhaTexto());
		registraConversorTipos(java.util.List.class, new ConversorPlanilhaList());
	}

	/**
	 * Faz a conversão dos Tipos Utilizados no XlsBeans
	 */
	public static Object converteXBValor(Class clazz, String valor, String nomeCampo) {
		if (valor == null) {
			return null;
		}

		Object valorConvertido = null;
		try {
			valorConvertido = getConversor(clazz).converteValor(clazz, valor);
		} catch (Exception e) {
			throw new GerenciadorTestException("Problema ao convertor o valor '" + valor.toString() + "', do campo '"
					+ nomeCampo + "' de classe '" + clazz.getSimpleName() + "'." + e.getMessage());
		}
		return valorConvertido;
	}

	public static ConversorPlanilha<?> getConversor(Class<?> clazz) {
		ConversorPlanilha<?> conversor = conversor(clazz);
		Preconditions.checkNotNull(conversor,
				"Não foi encontrado conversor para a classe: '" + clazz.getSimpleName() + "'.");
		return conversor;
	}

	private static ConversorPlanilha<?> conversor(Class<?> clazz) {
		if (clazz != null) {
			ConversorPlanilha<?> conversor = tipoConversor.get(clazz);
			if (conversor == null) {
				return conversor(clazz.getSuperclass());
			}
			return tipoConversor.get(clazz);
		}
		return null;
	}

}

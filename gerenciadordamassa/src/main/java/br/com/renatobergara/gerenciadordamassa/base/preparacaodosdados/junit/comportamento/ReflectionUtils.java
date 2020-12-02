package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.comportamento;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;

import br.com.renatobergara.gerenciadordamassa.base.exception.GerenciadorTestException;
import br.com.renatobergara.gerenciadordamassa.base.util.clazz.FieldTestUtils;
import br.com.renatobergara.gerenciadordamassa.base.util.clazz.FieldTestUtils.FieldDescriptor;

/**
 * Classe utilitaria, para fornecer ajuda com métodos de Reflexão.
 */
public class ReflectionUtils {

	private ReflectionUtils() {
		// Não faz nada
	}

	public static Class getClassByField(final String propriedade, final Class<?> clazz) {
		try {
			final FieldDescriptor fieldDescriptor = FieldTestUtils.getFieldDescriptor(clazz, propriedade);
			Preconditions.checkNotNull(fieldDescriptor,
					"Não foi encontrado o campo '" + propriedade + "', na classe '" + clazz.getSimpleName() + "'");
			Preconditions.checkNotNull(fieldDescriptor.getReadMethod(), "Não foi encontrado setter ou getter da'"
					+ propriedade + "', na classe '" + clazz.getSimpleName() + "'");
			if (fieldDescriptor.getType().isAssignableFrom(List.class)
					|| fieldDescriptor.getType().isAssignableFrom(Set.class)) {
				final ParameterizedType tipoRepositorio = (ParameterizedType) fieldDescriptor.getReadMethod()
						.getGenericReturnType();
				return (Class) tipoRepositorio.getActualTypeArguments()[0];
			}
			return fieldDescriptor.getType();
		} catch (final SecurityException e) {
			throw new GerenciadorTestException("Não foi encontrado Propriedade com o nome: " + propriedade);
		}
	}

	public static Method getClassByNestedProprerties(final Class<?> clazz, final String propertieNested,
			String[] args) {
		String[] properties = propertieNested.split("\\.");
		if (properties.length > 1) {
			String propertie = properties[1].substring(properties[1].indexOf(".") + 1, properties[1].length());
			return getClassByNestedProprerties(getMetodo(clazz, properties, new String[] {}).getReturnType(), propertie,
					args);
		}
		return getMetodo(clazz, properties, args);
	}

	public static Method getClassByNestedProprerties(final Class<?> clazz, final String propertieNested) {
		return getClassByNestedProprerties(clazz, propertieNested, new String[] {});
	}

	private static Method getMetodo(final Class<?> clazz, String[] properties, String[] args) {
		String[] nomenclaturas = new String[] { "get", "is", null };
		for (String nomenclatura : nomenclaturas) {
			Method method = getMetodoPorNome(clazz, nomenclatura, properties[0], args);
			if (method != null) {
				return method;
			}
		}
		throw new GerenciadorTestException("Método não encontrado com o nome 'get/is/nomePropriedade': " + properties[0]);
	}

	private static Method getMetodoPorNome(Class<?> clazz, String nomenclatura, String nomeMetodo, String[] args) {
		try {
			String metodo = nomeMetodo;
			if (nomenclatura != null) {
				metodo = nomenclatura + Character.toUpperCase(nomeMetodo.charAt(0)) + nomeMetodo.substring(1);
			}
			if (args.length == 0) {
				return getMetodoSemArgumento(clazz, metodo);
			}
			return getMetodoComArgumento(clazz, metodo, args);
		} catch (SecurityException e) {
			throw new GerenciadorTestException(e);
		}
	}

	private static Method getMetodoSemArgumento(Class<?> clazz, String metodo) {
		boolean metodosComMesmoNome = false;
		Method method = null;
		for (Method m : clazz.getMethods()) {
			if (m.getName().equals(metodo)) {
				method = m;
				if (metodosComMesmoNome) {
					throw new GerenciadorTestException("Existe mais de um método com o mesmo nome: '" + metodo
							+ "', com argumentos diferentes. Caso queira utilizar algum dos métodos, utilizar o padrão passando o nome da classe do argumento na ordem correta.");
				}
				metodosComMesmoNome = true;
			}
		}
		return method;
	}

	private static Method getMetodoComArgumento(Class<?> clazz, String metodo, String[] args) {
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(metodo) && method.getParameterTypes().length == args.length) {
				// Nome do método e número de argumento são iguais
				// então verificamos se os tipos dos argumentos são iguais.
				for (int i = 0; i < method.getParameterTypes().length; i++) {
					Class parametro = method.getParameterTypes()[i];
					if (!parametro.getSimpleName().equals(args[i])) {
						throw new GerenciadorTestException(
								"O método não foi encontrado, a ordem dos parametros dos argumentos não estão corretos.");
					}
				}
				return method;
			}
		}
		return null;
	}

	public static Field getField(final Class<?> voClazz, String fieldName) {
		Field field;
		Class<?> classe = voClazz;

		while (!classe.equals(Object.class)) {
			try {
				field = classe.getDeclaredField(fieldName);
				return field;

			} catch (SecurityException e) {
				throw new GerenciadorTestException(e);

			} catch (NoSuchFieldException e) {
				// Se não existe o atributo este método retorna null.
			}

			classe = classe.getSuperclass();
		}

		return null;
	}

}

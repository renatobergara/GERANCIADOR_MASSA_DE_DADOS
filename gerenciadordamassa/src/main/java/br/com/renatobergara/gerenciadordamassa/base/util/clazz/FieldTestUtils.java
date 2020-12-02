package br.com.renatobergara.gerenciadordamassa.base.util.clazz;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import br.com.renatobergara.gerenciadordamassa.base.exception.GerenciadorTestException;

/**
 * Classe que auxilia o uso de reflexão para trabalhar com propriedades de
 * classes dinamicamente.
 */
public class FieldTestUtils {

	/**
	 * Esta classe é um wrapper de uma propriedade e auxilia as rotinas que utilizam
	 * reflexão para varrer propriedades e trabalhar com metadados em propriedades e
	 * métodos.
	 */
	public static class FieldDescriptor {
		private final Field field;
		private final Class<?> voClazz;

		@Override
		public String toString() {
			return voClazz.getName();
		}

		/**
		 * 
		 * @param voClazz Classe do objeto (subclasse) que faz uso do campo
		 * @param field   O campo
		 */
		private FieldDescriptor(Class<?> voClazz, Field field) {
			this.field = field;
			this.voClazz = voClazz;
		}

		/**
		 * @return Nome do campo
		 */
		public String getName() {
			return field.getName();
		}

		/**
		 * Verifica se a annotation está no método getter correspondente a esta
		 * propriedade Caso não encontre a anotação no getter procura por ela na
		 * declaração do próprio campo
		 * 
		 * @param annotationClass - Classe da annotation
		 * @return [true] annotation está presente, [false] annotation não está presente
		 */
		public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
			boolean annotationPresent = false;

			// procura pela anotação no getter da propriedade
			Method readMethod = getReadMethod();
			if (readMethod != null) {
				annotationPresent = readMethod.isAnnotationPresent(annotationClass);
			}

			// se não encontrou tenta procurar na declaração do campo
			if (!annotationPresent) {
				annotationPresent = field.isAnnotationPresent(annotationClass);
			}
			return annotationPresent;
		}

		/**
		 * Verifica se a annotation está no método getter correspondente a esta
		 * propriedade Caso não encontre a anotação no getter procura por ela na
		 * declaração do próprio campo
		 * 
		 * @param annotationClass - Classe da annotation
		 * @return Annotation
		 */
		public Annotation getAnnotation(Class<? extends Annotation> annotationClass) {

			Annotation annotation = null;
			Method readMethod = getReadMethod();
			// procura pela anotação no getter da propriedade
			if (readMethod != null) {
				annotation = readMethod.getAnnotation(annotationClass);
			}

			// se não encontrou tenta procurar na declaração do campo
			if (annotation == null) {// ultima tentativa eh procurar no proprio field
				annotation = field.getAnnotation(annotationClass);
			}
			return annotation;
		}

		/**
		 * Obtém o método getter correspondente a esta propriedade, procurado
		 * recursivamente na hierarquia de classes
		 * 
		 * @return Método getter
		 */
		public Method getReadMethod() {
			String methodName = "get" + getMethodSufix();
			Method method = null;
			try {
				method = voClazz.getMethod(methodName);
			} catch (SecurityException e) {
				throw new GerenciadorTestException(e);

			} catch (NoSuchMethodException e) {
				// Se não existe o método getter este método retorna null.
			}

			if (method == null) {
				methodName = "is" + getMethodSufix();
				try {
					method = voClazz.getMethod(methodName);
				} catch (SecurityException e) {
					throw new GerenciadorTestException(e);

				} catch (NoSuchMethodException e) {
					// Se não existe o método getter este método retorna null.
				}
			}

			return method;
		}

		/**
		 * Obtém o método setter correspondente a esta propriedade, procurado
		 * recursivamente na hierarquia de classes
		 * 
		 * @return Método setter
		 */
		public Method getWriteMethod() {
			String methodName = "set" + getMethodSufix();
			Method method = null;
			try {
				method = voClazz.getMethod(methodName, field.getType());
			} catch (SecurityException e) {
				throw new GerenciadorTestException(e);

			} catch (NoSuchMethodException e) {
				// Se não existe o método setter este método retorna null.
			}

			return method;
		}

		/**
		 * Obtém o valor da propriedade através do método getter correspondente
		 * 
		 * @param vo - Objeto que possui a propriedade declarada
		 * @return Valor da Propriedade
		 */
		public Object getValue(Object vo) {
			Method getter = this.getReadMethod();
			if (getter == null) {
				throw new GerenciadorTestException("Método getter não encontrado para a propriedade: " + this.getName());
			}

			final Object value;

			try {
				value = getter.invoke(vo);

			} catch (Exception e) {
				throw new GerenciadorTestException(e);
			}

			return value;
		}

		public Class<?> getType() {
			return field.getType();
		}

		public Class<?> getTipoClass() {
			Class<?> clazz;
			if (this.field.getType().isAssignableFrom(List.class)) {
				final ParameterizedType tipoParametro = (ParameterizedType) this.field.getGenericType();
				clazz = (Class<?>) tipoParametro.getActualTypeArguments()[0];
			} else {
				clazz = this.field.getType();
			}
			return clazz;
		}

		private String getMethodSufix() {
			return Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
		}
	}

	/*
	 * Cache da lista de campos dos VO´s do Gateway. Este cache é alimentado pelo
	 * método getFieldList(). Uma vez que sabemos que são apenas poucos tipos de VO,
	 * não temos a preocupação de limpar o cache. Este cache é aquecido por várias
	 * threads durante a importação. Por isso sua implementação é sincronizada.
	 */
	private static Map<Class<?>, List<Field>> fieldListCache = Collections
			.synchronizedMap(new HashMap<Class<?>, List<Field>>());

	/**
	 * Recupera a lista de campos do VO de tipo type do cache. Caso ela não esteja
	 * cacheada, a criamos e inserimos no cache.
	 * 
	 * A implementação de mapa que utilizamos par ao cache é sincronizada. Ademais,
	 * não há problema em mais de uma thread construir a lista de campos de um
	 * determinado tipo de VO (todas as threads obteriam exatamente o mesmo
	 * resultado). Logo, não precisamos nos preocupar com concorrência neste método.
	 * 
	 * @param valueObject
	 * @return
	 */
	public static List<Field> getFieldList(final Class<?> voClazz) {

		// Tentamos obter a lista de campos do cache
		List<Field> voFields = fieldListCache.get(voClazz);

		if (voFields == null) {

			/*
			 * Não temos a lista de tipos cacheada. Precisamos criá-la e inseri-la no cache.
			 * Vamos utilizar reflexão para popular cada campo dessa lista com seu
			 * correspondente em parsedObj. Note que aqui precisamos incluir os campos de
			 * todas as superclasses (exceto Object).
			 */

			// Fazendo uma cópia do atributo para modificá-lo. Não é boa prática
			// modificar parâmetros
			Class<?> clazz = voClazz;
			voFields = new ArrayList<Field>(); // A nova lista de atributos do
			// VO

			/*
			 * Percorremos a hierarquia de classes até atingir Object, adicionando os campos
			 * de cada classe a voFields.
			 */
			while (!clazz.equals(Object.class)) {
				final Field[] fields = clazz.getDeclaredFields();

				Collections.addAll(voFields, fields);

				clazz = clazz.getSuperclass();
			}

			// Adicionando a nova lista de campos ao cache
			fieldListCache.put(voClazz, voFields);
		}

		return voFields;

	}

	/**
	 * Recupera a lista de campos de uma classe e superclasses via reflexão. A lista
	 * de campos vem ordenada de acordo com a hierarquia de classes, sendo os campos
	 * das classes abstratas os primeiros da lista. Ex: Se A extends B e B extends
	 * C, a ordem dos campos na lista será: campos de A, campos de B e por último os
	 * campos de C.
	 * 
	 * @param voClazz
	 * @return
	 */
	public static List<FieldDescriptor> getAllFieldDescriptors(final Class<?> voClazz) {
		List<List<FieldDescriptor>> fieldDescriptorsByClass = Lists.newArrayList();

		Class<?> classe = voClazz;

		while (!classe.equals(Object.class)) {
			Field[] fields = classe.getDeclaredFields();

			/*
			 * Fazendo a busca por todos os campos, mas matendo a classe para a qual o
			 * FieldDescriptor é criado sempre como a classe "folha" => a classe do VO
			 * passada pelo método Essa busca não utiliza a função
			 * 'getFieldDescriptors(final Class<?> voClazz)' mais abaixo justamente para
			 * poder manter a referência dos FielDescritors sempre apontando para a classe
			 * folha.
			 */
			List<FieldDescriptor> fieldDescriptors = new ArrayList<FieldDescriptor>();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				if (!field.isSynthetic()) {
					// criando o FieldDescriptor sempre apontando para a classe 'folha'
					fieldDescriptors.add(new FieldDescriptor(voClazz, field));
				}
			}
			fieldDescriptorsByClass.add(fieldDescriptors);

			classe = classe.getSuperclass();
		}

		Collections.reverse(fieldDescriptorsByClass);

		List<FieldDescriptor> allFieldDescriptors = Lists.newArrayList();

		for (List<FieldDescriptor> fieldDescriptors : fieldDescriptorsByClass) {
			allFieldDescriptors.addAll(fieldDescriptors);
		}

		return allFieldDescriptors;
	}

	/**
	 * Recupera a lista de campos de uma classe via reflexão. Este método NÃO
	 * recupera os campos declarados em superclasses, considera apenas os campos
	 * declarados na classe passada por parâmetro. Os campos sintéticos (campos que
	 * não aparecem no código fonte original) também não são retornados.
	 * 
	 * @param voClazz
	 * @return
	 */
	public static List<FieldDescriptor> getFieldDescriptors(final Class<?> voClazz) {
		List<FieldDescriptor> fieldDescriptors = new ArrayList<FieldDescriptor>();
		List<Field> fields = getTodosCamposHierarquiaClasse(voClazz);
		for (Field field : fields) {
			if (!field.isSynthetic()) {
				fieldDescriptors.add(new FieldDescriptor(voClazz, field));
			}
		}
		return fieldDescriptors;
	}

	private static List<Field> getTodosCamposHierarquiaClasse(final Class<?> voClazz) {
		List<Field> resultado = Lists.newArrayList(voClazz.getDeclaredFields());
		Class<?> classe = voClazz.getSuperclass();
		while (!(classe.equals(Object.class))) {
			resultado.addAll(Lists.newArrayList(classe.getDeclaredFields()));
			classe = classe.getSuperclass();
		}
		return resultado;
	}

	public static List<FieldDescriptor> getFieldDescriptorsByAnnotation(final Class<?> voClazz,
			final Class<? extends Annotation> annotationClass) {
		List<FieldDescriptor> fieldDescriptors = getFieldDescriptors(voClazz);
		return Lists.newArrayList(Collections2.filter(fieldDescriptors, new Predicate<FieldDescriptor>() {
			@Override
			public boolean apply(FieldDescriptor fieldDescriptor) {
				return fieldDescriptor.isAnnotationPresent(annotationClass);
			}
		}));
	}

	public static FieldDescriptor getFieldDescriptorByAnnotation(final Class<?> voClazz,
			final Class<? extends Annotation> annotationClass) {
		List<FieldDescriptor> fieldDescriptors = getFieldDescriptorsByAnnotation(voClazz, annotationClass);
		if (fieldDescriptors.isEmpty()) {
			return null;
		}
		if (fieldDescriptors.size() > 1) {
			throw new GerenciadorTestException("Existe mais de um campo com a Anotação" + annotationClass.getName());
		}
		return fieldDescriptors.get(0);
	}

	/**
	 * Recupera um atributo de uma classe via reflexão.
	 * 
	 * @param voClazz
	 * @return
	 */
	public static FieldDescriptor getFieldDescriptor(final Class<?> voClazz, String fieldName) {
		Field field;
		Class<?> classe = voClazz;

		while (!classe.equals(Object.class)) {
			try {
				field = classe.getDeclaredField(fieldName);
				return new FieldDescriptor(voClazz, field);

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

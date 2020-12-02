package br.com.renatobergara.gerenciadordamassa.base.xlsbeans.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBComponentsRecords;
import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBIdParent;
import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBIdentifier;
import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBIgnoreConverter;
import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBManyToOne;
import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBOneToMany;
import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBOneToOne;
import br.com.renatobergara.gerenciadordamassa.base.conversor.ConversorUtils;
import br.com.renatobergara.gerenciadordamassa.base.exception.GerenciadorTestException;
import br.com.renatobergara.gerenciadordamassa.base.exception.xlsbeans.GerenciadorXlsBeansException;
import br.com.renatobergara.gerenciadordamassa.base.util.clazz.FieldTestUtils;
import br.com.renatobergara.gerenciadordamassa.base.util.clazz.FieldTestUtils.FieldDescriptor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import net.java.amateras.xlsbeans.NeedPostProcess;
import net.java.amateras.xlsbeans.Utils;
import net.java.amateras.xlsbeans.XLSBeansException;
import net.java.amateras.xlsbeans.annotation.RecordTerminal;
import net.java.amateras.xlsbeans.processor.FieldProcessor;
import net.java.amateras.xlsbeans.processor.HeaderInfo;
import net.java.amateras.xlsbeans.xml.AnnotationReader;
import net.java.amateras.xlsbeans.xssfconverter.WBorder;
import net.java.amateras.xlsbeans.xssfconverter.WBorderLineStyle;
import net.java.amateras.xlsbeans.xssfconverter.WCell;
import net.java.amateras.xlsbeans.xssfconverter.WCellFormat;
import net.java.amateras.xlsbeans.xssfconverter.WSheet;

/**
 * Responsável por montar as estruturas dos objetos conforme a Tabela da
 * Planilha.
 */
public class GerenciamentoHorizontalProcessor implements FieldProcessor {

	@Override
	public void doProcess(WSheet wSheet, Object obj, Method setter, Annotation ann, AnnotationReader reader,
			List<NeedPostProcess> needPostProcess) throws Exception {

		Class<?>[] clazzes = setter.getParameterTypes();
		if (clazzes.length != 1) {
			throw new GerenciadorXlsBeansException("Argumento do '" + setter.toString() + "' é inválido.");
		} else if (List.class.isAssignableFrom(clazzes[0])) {
			RepositorioPlanilha<?> repositorio = (RepositorioPlanilha<?>) obj;
			List<?> value = createRecords(wSheet, (XBComponentsRecords) ann, reader, needPostProcess,
					repositorio.getTipo(), repositorio.getPacotePlanilha(), repositorio.getClasseTeste(),
					componentesPorPlanilha());
			if (value != null) {
				setter.invoke(obj, new Object[] { value });
			}
		} else {
			throw new GerenciadorXlsBeansException("Argumento do '" + setter.toString() + "' é inválido.");
		}

	}

	@Override
	public void doProcess(WSheet wSheet, Object obj, Field field, Annotation ann, AnnotationReader reader,
			List<NeedPostProcess> needPostProcess) throws Exception {
		Class<?> clazz = field.getType();
		if (List.class.isAssignableFrom(clazz)) {
			RepositorioPlanilha<?> repositorio = (RepositorioPlanilha<?>) obj;
			List<?> value = createRecords(wSheet, (XBComponentsRecords) ann, reader, needPostProcess,
					repositorio.getTipo(), repositorio.getPacotePlanilha(), repositorio.getClasseTeste(),
					componentesPorPlanilha());
			if (value != null) {
				field.setAccessible(true);
				field.set(obj, value);
			}
		} else {
			throw new GerenciadorXlsBeansException("Argumento do '" + field.toString() + "' é inválido.");
		}
	}

	private List<?> createRecords(WSheet wSheet, XBComponentsRecords records, AnnotationReader reader,
			List<NeedPostProcess> needPostProcess, Class<?> clazz, Package pacote, Class<?> classeTeste,
			LoadingCache<ChaveTabelaComponente, List<?>> elementosComponentesPorTipo) throws Exception {

		List<Object> result = Lists.newArrayList();
		List<HeaderInfo> headers = Lists.newArrayList();

		// get header
		int initColumn = -1;
		int initRow = -1;
		String tableLabel = clazz.getSimpleName();
		if (tableLabel.equals("")) {
			initColumn = records.headerColumn();
			initRow = records.headerRow();
		} else {
			try {
				WCell labelCell = Utils.getCell(wSheet, tableLabel, 0);
				initColumn = labelCell.getColumn();
				initRow = labelCell.getRow() + records.bottom();
			} catch (XLSBeansException ex) {
				if (records.optional()) {
					return null;
				}
				throw ex;
			}
		}

		int hColumn = initColumn;
		int hRow = initRow;
		int rangeCount = 1;

		while (true) {
			try {
				WCell cell = wSheet.getCell(hColumn, hRow);
				while (cell.getContents().equals("") && rangeCount < records.range()) {
					cell = wSheet.getCell(hColumn + rangeCount, hRow);
					rangeCount++;
				}
				if (cell.getContents().equals("")) {
					break;
				}
				headers.add(new HeaderInfo(cell.getContents(), rangeCount - 1));
				hColumn = hColumn + rangeCount;
				rangeCount = 1;
			} catch (ArrayIndexOutOfBoundsException ex) {
				break;
			}
			if (records.headerLimit() > 0 && headers.size() >= records.headerLimit()) {
				break;
			}
		}

		RecordTerminal terminal = records.terminal();
		if (terminal == null) {
			terminal = RecordTerminal.Empty;
		}

		// get records
		hRow++;
		while (hRow < wSheet.getRows()) {
			hColumn = initColumn;
			boolean emptyFlag = true;
			Object record = clazz.newInstance();

			for (int i = 0; i < headers.size() && hRow < wSheet.getRows(); i++) {
				HeaderInfo headerInfo = headers.get(i);
				hColumn = hColumn + headerInfo.getHeaderRange();
				WCell wCell = wSheet.getCell(hColumn, hRow);

				// find end of the table
				if (!wCell.getContents().equals("")) {
					emptyFlag = false;
				}
				if (terminal == RecordTerminal.Border && i == 0) {
					WCellFormat wFormat = wCell.getCellFormat();
					if (wFormat != null && !wFormat.getBorder(WBorder.LEFT).equals(WBorderLineStyle.NONE)) {
						emptyFlag = false;
					} else {
						emptyFlag = true;
						break;
					}
				}
				if (!records.terminateLabel().equals("")) {
					if (wCell.getContents().equals(records.terminateLabel())) {
						emptyFlag = true;
						break;
					}
				}

				Field field = this.getField(record, headerInfo.getHeaderLabel());
				if (field != null) {
					// Se ha a anotação, os campos vem com o valor vazio, ao invés de null.
					// Utilizada nos testes funcionais
					this.setValor(record, Strings.nullToEmpty(wCell.getContents()), field, pacote, classeTeste);
				}

				hColumn++;
			}
			if (emptyFlag) {
				break;
			}
			result.add(record);
			for (Field field : record.getClass().getDeclaredFields()) {
				this.criaMapeamentoComponenteElementos(wSheet, reader, needPostProcess, record, field, pacote,
						classeTeste, elementosComponentesPorTipo);
			}
			hRow++;
		}
		this.criaRelacionamentoUmParaMuitos(result, pacote, classeTeste);
		return result;
	}

	private void criaRelacionamentoUmParaMuitos(List<Object> result, Package pacote, Class<?> classeTeste)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		for (Object object : result) {
			List<FieldDescriptor> descriptors = FieldTestUtils.getFieldDescriptorsByAnnotation(object.getClass(),
					XBOneToMany.class);
			if (!descriptors.isEmpty()) {
				FieldDescriptor descriptor = FieldTestUtils.getFieldDescriptorByAnnotation(object.getClass(),
						XBIdentifier.class);
				Preconditions.checkNotNull(descriptor, "Não existe campo identificador nos Objetos de Relacionamento");
				for (FieldDescriptor fieldDescriptor : descriptors) {
					List<Object> resultado = Lists.newArrayList();
					RepositorioPlanilha<?> repositorio = RepositorioPlanilhaFactory.of().loadByBean(pacote,
							fieldDescriptor.getTipoClass(), classeTeste);
					for (Object elementoRep : repositorio.getElementos()) {
						Object pk = descriptor.getValue(object);
						Object fk = this.getElementManyToOne(elementoRep, object.getClass());
						// Comparação sempre será feita por objetos String's!!!
						if (pk.equals(fk)) {
							resultado.add(elementoRep);
						}
					}
					fieldDescriptor.getWriteMethod().invoke(object, resultado);
				}
			}
		}
	}

	private void setValor(Object record, String valor, final Field field, Package pacote, Class<?> classeTeste)
			throws IllegalAccessException, Exception {
		if (field.isAnnotationPresent(XBOneToOne.class) && (valor != null)) {
			montaRelacionamentoOneToOneEntrePlanilhasDiferentes(record, valor, field, pacote, classeTeste);
		} else {
			setaValoresGerenciador(record, valor, field);
		}
	}

	private void setaValoresGerenciador(Object record, String valor, final Field field) throws IllegalAccessException {
		field.setAccessible(true);
		if (valor != null) {
			if (record.getClass().isAnnotationPresent(XBIgnoreConverter.class)
					|| field.isAnnotationPresent(XBIgnoreConverter.class)) {
				field.set(record, valor);
			} else {
				Object valorConvertido = ConversorUtils.converteXBValor(field.getType(), valor, field.getName());
				field.set(record, valorConvertido);
			}

		}
	}

	private void montaRelacionamentoOneToOneEntrePlanilhasDiferentes(Object record, String valor, final Field field,
			Package pacote, Class<?> classeTeste) throws IllegalAccessException, InvocationTargetException {
		// Nesse caso temos que injetar um tipo de outro repositório.
		Object elementoRepositorio = RepositorioPlanilhaFactory.of().loadByBean(pacote, field.getType(), classeTeste)
				.findElementById(valor);
		if (elementoRepositorio != null) {
			FieldDescriptor descriptor = FieldTestUtils.getFieldDescriptor(record.getClass(), field.getName());
			descriptor.getWriteMethod().invoke(record, elementoRepositorio);
		}
	}

	private void criaMapeamentoComponenteElementos(WSheet wSheet, AnnotationReader reader,
			List<NeedPostProcess> needPostProcess, Object record, Field field, Package pacote, Class<?> classeTeste,
			LoadingCache<ChaveTabelaComponente, List<?>> elementosComponentesPorTipo)
			throws Exception, XLSBeansException, IllegalAccessException, InvocationTargetException {
		XBComponentsRecords registrosHorizontal = reader.getAnnotation(record.getClass(), field,
				XBComponentsRecords.class);
		if (registrosHorizontal != null) {

			List<?> registrosTabela = elementosComponentesPorTipo.get(new ChaveTabelaComponente(wSheet,
					registrosHorizontal, reader, needPostProcess, field, pacote, classeTeste));
			FieldDescriptor descriptorPk = FieldTestUtils.getFieldDescriptorByAnnotation(record.getClass(),
					XBIdentifier.class);
			Preconditions.checkNotNull(descriptorPk,
					"Campo Chave Obrigatório na classe: " + record.getClass().getName());
			List<Object> resultado = Lists.newArrayList();
			// Valor da chave do Objeto que contém a Coleção.
			String valorPk = (String) descriptorPk.getReadMethod().invoke(record);
			for (Object registroTabela : registrosTabela) {
				String valorFk = (String) this.getElementIdParent(registroTabela, record.getClass());
				if (valorPk.equals(valorFk)) {
					resultado.add(registroTabela);
				}
			}
			// Aqui verificamos se o mapeamento de Componentes deve ser feito por coleção.
			if (field.getType().isAssignableFrom(List.class)) {
				field.setAccessible(true);
				field.set(record, resultado);
			} else if (!resultado.isEmpty()) {
				Preconditions.checkArgument(resultado.size() == 1,
						"Existe mais de um Registro na Tabela de Relacionamento com a Chave:" + valorPk);
				field.setAccessible(true);
				field.set(record, resultado.get(0));
			}
		}

	}

	private Class<?> getTipoClass(Field field) {
		Class<?> clazz;
		if (field.getType().isAssignableFrom(List.class)) {
			final ParameterizedType tipoParametro = (ParameterizedType) field.getGenericType();
			clazz = (Class<?>) tipoParametro.getActualTypeArguments()[0];
		} else {
			clazz = field.getType();
		}
		return clazz;
	}

	private Field getField(Object obj, String name) {
		List<Field> fields = this.getTodosCamposHierarquiaClasse(obj);
		for (Field field : fields) {
			if (name.equals(field.getName())) {
				return field;
			}
		}
		return null;
	}

	private List<Field> getTodosCamposHierarquiaClasse(Object obj) {
		List<Field> resultado = Lists.newArrayList(obj.getClass().getDeclaredFields());
		Class<?> classe = obj.getClass().getSuperclass();
		while (!(classe.equals(Object.class))) {
			resultado.addAll(Lists.newArrayList(classe.getDeclaredFields()));
			classe = classe.getSuperclass();
		}
		return resultado;
	}

	private Object getElementIdParent(Object elementoRepositorio, Class<?> classeParent) {
		List<FieldDescriptor> descriptors = FieldTestUtils
				.getFieldDescriptorsByAnnotation(elementoRepositorio.getClass(), XBIdParent.class);
		Preconditions.checkArgument(!descriptors.isEmpty(),
				"Não existe campo com anotação @XBIdParent nos Objetos de Relacionamento da classe: "
						+ elementoRepositorio.getClass().getName());
		for (FieldDescriptor descriptor : descriptors) {
			final XBIdParent idParent = (XBIdParent) descriptor.getAnnotation(XBIdParent.class);
			for (Class<?> classe : idParent.classeParent()) {
				if (classe.equals(classeParent)) {
					return descriptor.getValue(elementoRepositorio);
				}
			}
		}
		throw new GerenciadorTestException(
				"Não foi encontrado um campo com a anotação @XBIdParent que define seu atributo classeParent com valor :"
						+ classeParent.getName() + ". O campo está sendo mapeado com a classe" + descriptors);
	}

	private Object getElementManyToOne(Object elementoRepositorio, Class<?> classePk) {
		List<FieldDescriptor> descriptors = FieldTestUtils
				.getFieldDescriptorsByAnnotation(elementoRepositorio.getClass(), XBManyToOne.class);
		Preconditions.checkArgument(!descriptors.isEmpty(),
				"Não existe campo com anotação @XBManyToOne nos Objetos de Relacionamento da classe: "
						+ elementoRepositorio.getClass().getName());
		for (FieldDescriptor descriptor : descriptors) {
			final XBManyToOne manyToOne = (XBManyToOne) descriptor.getAnnotation(XBManyToOne.class);
			for (Class<?> classe : manyToOne.classeFK()) {
				if (classe.equals(classePk)) {
					return descriptor.getValue(elementoRepositorio);
				}
			}
		}
		throw new GerenciadorTestException(
				"Não foi encontrado um campo com a anotação @XBManyToOne que define seu atributo classeFK com valor :"
						+ classePk.getName());
	}

	private LoadingCache<ChaveTabelaComponente, List<?>> componentesPorPlanilha() {
		return Caffeine.newBuilder().build(new CacheLoader<ChaveTabelaComponente, List<?>>() {
			@Override
			public List<?> load(final ChaveTabelaComponente from) throws Exception {
				try {
					return createRecords(from.wSheet, from.registrosHorizontal, from.reader, from.needPostProcess,
							getTipoClass(from.field), from.pacote, from.classeTeste, componentesPorPlanilha());
				} catch (RuntimeException e) {
					Throwables.throwIfUnchecked(e);
					return Arrays.asList();
				}
			}
		});
	}

	@RequiredArgsConstructor
	@EqualsAndHashCode
	private class ChaveTabelaComponente {
		final WSheet wSheet;
		final XBComponentsRecords registrosHorizontal;
		final AnnotationReader reader;
		// TODO Não usamos esse cara, podemos futuramente ter problema com
		// equals/hashcode aqui?
		final List<NeedPostProcess> needPostProcess;
		final Field field;
		final Package pacote;
		final Class<?> classeTeste;
	}
}

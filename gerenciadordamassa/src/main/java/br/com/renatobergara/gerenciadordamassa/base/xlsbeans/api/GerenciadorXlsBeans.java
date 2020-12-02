package br.com.renatobergara.gerenciadordamassa.base.xlsbeans.api;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBComponentsRecords;
import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBIdentifier;
import br.com.renatobergara.gerenciadordamassa.base.exception.GerenciadorTestException;
import br.com.renatobergara.gerenciadordamassa.base.util.clazz.FieldTestUtils;
import br.com.renatobergara.gerenciadordamassa.base.util.clazz.FieldTestUtils.FieldDescriptor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.java.amateras.xlsbeans.XLSBeans;
import net.java.amateras.xlsbeans.XLSBeansException;
import net.java.amateras.xlsbeans.annotation.Sheet;
import net.java.amateras.xlsbeans.processor.FieldProcessorFactory;
import net.java.amateras.xlsbeans.xml.AnnotationReader;

/**
 * Ponto de entrada para a leitura da Planilha utilizando o XlsBeans.
 * 
 * @param <T> - Tipo da Classe do Elemento da Planilha
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GerenciadorXlsBeans<T> extends XLSBeans {

	private final RepositorioPlanilha<T> repositorio;

	static {
		GerenciamentoHorizontalProcessor processor = new GerenciamentoHorizontalProcessor();
		FieldProcessorFactory.registerProcessor(XBComponentsRecords.class, processor);
	}

	public static <T> GerenciadorXlsBeans<T> loadRepositorio(final RepositorioPlanilha<T> repositorio) {
		return new GerenciadorXlsBeans<T>(repositorio);
	}

	public static <T> GerenciadorXlsBeans<T> loadRepositorioByBean(final Package pacote, final Class<T> classBean,
			Class<?> classeTeste) {
		return new GerenciadorXlsBeans<T>(pacote, classBean, classeTeste);
	}

	private GerenciadorXlsBeans(final Package pacote, final Class<T> classBean, Class<?> classeTeste) {
		this.repositorio = new RepositorioDefault(classBean, pacote, classeTeste);
	}

	private GerenciadorXlsBeans(final RepositorioPlanilha<T> repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public <P> Sheet getSheetAnnotation(AnnotationReader reader, Class<P> clazz) throws Exception {
		return this.repositorio.getTipo().getAnnotation(Sheet.class);
	}

	@SuppressWarnings("unchecked")
	public <P extends RepositorioPlanilha<T>> RepositorioPlanilha<T> loadRepositorio(InputStream xlsIn)
			throws XLSBeansException {
		return super.load(xlsIn, this.repositorio.getClass());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P> P newInstance(Class<P> clazz) {
		return (P) this.repositorio;
	}

	@Data
	@EqualsAndHashCode(callSuper = false)
	private class RepositorioDefault extends RepositorioAbs<T> {
		private final Class<T> clazz;
		private final Package pacote;
		private final Class<?> classeTeste;
		@XBComponentsRecords
		private List<T> elementos;

		@Override
		public final List<T> getElementos() {
			this.validaChaves();
			return this.elementos;
		}

		@Override
		public Class<?> getClasseTeste() {
			return this.classeTeste;
		}

		@Override
		public Class<T> getTipo() {
			return this.clazz;
		}

		@Override
		public Package getPacotePlanilha() {
			return this.pacote;
		}

		private void validaChaves() {
			Map<String, T> result = Maps.newHashMap();
			for (T el : this.elementos) {
				FieldDescriptor descriptorPk = FieldTestUtils.getFieldDescriptorByAnnotation(el.getClass(),
						XBIdentifier.class);
				Preconditions.checkNotNull(descriptorPk,
						Joiner.on(',').join("Faltou id: ", el.getClass().getName(), pacote));
				String id = (String) descriptorPk.getValue(el);
				if (result.containsKey(id)) {
					throw new GerenciadorTestException(
							Joiner.on(',').join("Elementos de mesmo tipo na planilha com mesmo ID", clazz, pacote));
				}
				result.put(id, el);
			}
		}
	}
}

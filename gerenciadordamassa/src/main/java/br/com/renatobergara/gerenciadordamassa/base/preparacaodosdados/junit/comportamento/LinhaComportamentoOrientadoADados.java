package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.comportamento;

import java.beans.PropertyEditor;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import br.com.renatobergara.gerenciadordamassa.base.annotation.InjetaAutomatico;
import br.com.renatobergara.gerenciadordamassa.base.exception.GerenciadorTestException;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.cargadedados.CarregaDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.LinhaDeDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.ValorDosElementos;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.api.ComportamentoOrientadoADados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.api.ComportamentoOrientadoADadosJUnitTestCase;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.editor.EditorDePropriedadeString;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.spring.scope.EscopoLinhaDoTeste;
import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * Encapsula o comportamento do metodo que instância a linha do caso de teste,
 * que representa uma linha de dados e elementos de teste.
 * 
 */
public class LinhaComportamentoOrientadoADados extends ComportamentoOrientadoADadosBase
		implements ComportamentoOrientadoADados {

	/**
	 * Logger.
	 */
	protected static final Logger LOG = LoggerFactory.getLogger(LinhaComportamentoOrientadoADados.class);

	/**
	 * Member: id da linha.
	 */
	protected String idLinha;

	/**
	 * Propriedades Default.
	 */
	protected Properties propriedades;

	/**
	 * Dependencia da injeção no construtor.
	 * 
	 * @param testCase
	 * @param nomeDoMetodo
	 * @param idLinha
	 * @param carregaDados
	 */
	public LinhaComportamentoOrientadoADados(TestCase testCase, String nomeDoMetodo, String idLinha,
			CarregaDados carregaDados) {
		super(testCase, nomeDoMetodo, carregaDados);

		Validate.notNull(idLinha, "O Argumento idLinha não pode ser nulo.");
		this.idLinha = idLinha;

	}

	public void setUp(ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase)
			throws Exception {

		// Hook: Before data setup
		comportamentoOrientadoADadosJUnitTestCase.setUpBeforeData();

		// Populate with data -----------------

		BeanWrapper beanWrapper = wrapBean(testeCase);

		// TODO Remove properties once refactored into the dataloader
		setUpProperties(this.propriedades, beanWrapper);

		LinhaDeDados linha = carregaDados.carregarLinhas(testeCase, nomeDoMetodo, idLinha);
		setUpData(linha, beanWrapper);

		// Hook: After data setup
		comportamentoOrientadoADadosJUnitTestCase.setUpAfterData();
	}

	/**
	 * @param testCase
	 * @return Um wrapper.
	 */
	protected BeanWrapper wrapBean(TestCase testCase) {

		BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl(testCase);

		PropertyEditor editor = new EditorDePropriedadeString();
		beanWrapperImpl.registerCustomEditor(String.class, editor);

		return beanWrapperImpl;
	}

	/**
	 * Popula o teste com os dados e elementos.
	 *
	 * Utiliza o caso de teste como um argumento para o construtor.
	 * 
	 * @param linha       Input da linha não pode ser nula.
	 * @param beanWrapper
	 * @throws Exception Se falhar a carga de dados.
	 */
	@SuppressWarnings("unchecked")
	protected void setUpData(LinhaDeDados linha, BeanWrapper beanWrapper) throws Exception {
		if (beanWrapper.getWrappedClass().isAnnotationPresent(InjetaAutomatico.class)) {
			List<Campo> tipoCampos = this.orgazinaTipos(linha, beanWrapper);
			for (Campo campo : tipoCampos) {
				beanWrapper.setPropertyValue(campo.getPropriedade(), campo.getValorConvertido());
			}
		} else {
			throw new GerenciadorTestException("Falta a anotação @InjetaAutomatico na classe de teste. ");
		}
	}

	/**
	 * Preenche este objeto com os elementos das propriedades.
	 * 
	 * @param propriedades As propriedades podem ser nulas.
	 * @param beanWrapper
	 * @throws Exception Se falhar o carregamento
	 */
	protected void setUpProperties(Properties propriedades, BeanWrapper beanWrapper) throws Exception {

		if (propriedades == null) {
			return;
		}

		// Se quisermos obter tudo, incluindo padrões de propriedades do objeto,
		// precisamos fazer assim, ou então não obteremos os padrões.
		for (Enumeration<?> propNameEnum = propriedades.propertyNames(); propNameEnum.hasMoreElements();) {

			String key = (String) propNameEnum.nextElement();
			Object value = propriedades.getProperty(key);

			if (LOG.isDebugEnabled()) {
				LOG.debug("[setUpProperties()] Entrada de propriedades: Nome '" + key + "' = valor '" + value + "'");
			}

			try {
				beanWrapper.setPropertyValue(key, value);
			} catch (NotReadablePropertyException e) {
				LOG.debug("Ignorando propriedade que não foi encontrada em seu caso de teste.", e);
			} catch (NotWritablePropertyException e) {
				LOG.debug("Ignorando propriedade que não foi encontrada em seu caso de teste.", e);
			}
		}
	}

	public void tearDown(ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase)
			throws Exception {
		comportamentoOrientadoADadosJUnitTestCase.tearDownBeforeData();
		comportamentoOrientadoADadosJUnitTestCase.tearDownAfterData();
	}

	/**
	 * A linha é apenas um caso de teste..
	 */
	public int countTestCases() {
		return 1;
	}

	/**
	 * Esta linha deve ser executada como uma instância de caso de teste JUnit.
	 */
	public void run(TestResult resultado,
			ComportamentoOrientadoADadosJUnitTestCase comportamentoOrientadoADadosJUnitTestCase) {
		comportamentoOrientadoADadosJUnitTestCase.superRun(resultado);
	}

	/**
	 * @return Properties
	 */
	public Properties getProperties() {
		return propriedades;
	}

	/**
	 * @param propriedades
	 */
	public void setProperties(Properties propriedades) {
		this.propriedades = propriedades;
	}

	public void endTest(ComportamentoOrientadoADadosJUnitTestCase testCase) {
		EscopoLinhaDoTeste.terminaALinhaDoTeste(testCase.getName());
	}

	public void startTest(ComportamentoOrientadoADadosJUnitTestCase testCase) {
		EscopoLinhaDoTeste.iniciaALinhaDoTeste(testCase.getName());
	}

	private List<Campo> orgazinaTipos(final LinhaDeDados linhas, final BeanWrapper beanWrapper) {
		Preconditions.checkNotNull(linhas);
		List<Campo> elementos = Lists.newArrayList();
		for (final Iterator<?> iter = linhas.iterator(); iter.hasNext();) {
			final ValorDosElementos valorDosElemento = (ValorDosElementos) iter.next();
			Campo campo = Campos.of(valorDosElemento.getNome(), valorDosElemento.getValor(), beanWrapper);
			if (campo != null) {
				elementos.add(campo);
			}
		}
		return elementos;
	}

}

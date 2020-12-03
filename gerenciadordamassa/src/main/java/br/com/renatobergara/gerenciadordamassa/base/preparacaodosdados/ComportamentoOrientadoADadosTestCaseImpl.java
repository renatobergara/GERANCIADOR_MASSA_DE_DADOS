package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados;

import java.beans.PropertyEditor;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.cargadedados.CarregaDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.api.ComportamentoOrientadoADados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.api.ComportamentoOrientadoADadosJUnitTestCase;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.comportamento.StaticComportamentoFactory;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.utils.NomeTestCaseUtils;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.utils.TesteCaseInfo;
import junit.framework.TestCase;
import junit.framework.TestResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class ComportamentoOrientadoADadosTestCaseImpl extends TestCase
		implements ComportamentoOrientadoADadosJUnitTestCase {
	protected static final Logger logger = LoggerFactory.getLogger(ComportamentoOrientadoADadosTestCaseImpl.class);

	String fullName;

	@Getter(value = AccessLevel.PUBLIC)
	@Setter
	private String nomeCenario;

	@Getter(value = AccessLevel.PUBLIC)
	@Setter
	private String descricaoCenarioCompleto;

	@Getter(value = AccessLevel.PUBLIC)
	@Setter
	private String resultadoEsperado;

	ComportamentoOrientadoADados behaviour;

	@Override
	public final int countTestCases() {
		logger.info("Quantidade de casos de testes: " + getComportamento().countTestCases());
		return getComportamento().countTestCases();
	}

	@Override
	public final void run(TestResult resultado) {
		logger.info("Iniciando a execução do teste.");
		getComportamento().run(resultado, this);
	}

	@Override
	@Before
	public final void setUp() throws Exception {
		logger.info("Iniciando o setUP do teste.");
		getComportamento().setUp(this);
	}

	@Override
	@After
	public final void tearDown() throws Exception {
		logger.info("Finalizando o tearDown do teste.");
		getComportamento().tearDown(this);
	}

	@Override
	public void superRun(TestResult resultado) {
		super.run(resultado);
	}

	@Override
	@Before
	public void superSetUp() throws Exception {
		super.setUp();
	}

	@Override
	@After
	public void superTearDown() throws Exception {
		super.tearDown();
	}

	@Override
	public final void setName(String nome) {
		logger.info("Nome do Método[linha]: " + nome);

		if (!StringUtils.equals(nome, this.fullName)) {
			TesteCaseInfo info = NomeTestCaseUtils.parseNomeTestCase(nome);
			super.setName(info.getNomeDoMetodo());
			this.fullName = nome;
			this.behaviour = null;
		}
	}

	@Override
	public final String getName() {
		return this.fullName;
	}

	@Override
	public String toString() {
		return getClass().getName() + "#" + this.fullName;
	}

	ComportamentoOrientadoADados getComportamento() {
		if (this.behaviour == null) {
			if (StringUtils.isBlank(this.fullName)) {
				throw new IllegalStateException(
						"setName() não foi chamado. Este método não pode ser usado antes de setName ().");
			}

			CarregaDados caarregaDados = criarCarregadorDeDados();

			if (caarregaDados == null) {
				throw new IllegalStateException(
						"Sua implementação do método abstrato createDataLoader() retornou null. Não faça isso!");
			}

			this.behaviour = StaticComportamentoFactory.getComportamento(this, caarregaDados);
		}

		return this.behaviour;
	}

	public abstract CarregaDados criarCarregadorDeDados();

	@Override
	@Before
	public void setUpMethod() throws Exception {
	}

	@Override
	@Before
	public void setUpBeforeData() throws Exception {
	}

	@Override
	@After
	public void setUpAfterData() throws Exception {
	}

	@Override
	public void tearDownBeforeData() throws Exception {
	}

	@Override
	@After
	public void tearDownAfterData() throws Exception {
	}

	@Override
	public void tearDownMethod() throws Exception {
	}

	@Override
	public PropertyEditor[] getCustomPropertyEditors() {
		return null;
	}

}

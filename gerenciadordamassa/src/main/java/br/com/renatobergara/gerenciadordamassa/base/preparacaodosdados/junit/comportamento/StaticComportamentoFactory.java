package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.comportamento;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderSupport;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.cargadedados.CarregaDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.api.ComportamentoOrientadoADados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.utils.NomeTestCaseUtils;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.utils.TesteCaseInfo;
import junit.framework.TestCase;

public class StaticComportamentoFactory extends PropertiesLoaderSupport {
	protected static final Logger logger = LoggerFactory.getLogger(StaticComportamentoFactory.class);

	protected static StaticComportamentoFactory INSTANCIA;

	protected Properties propriedades;

	public static synchronized StaticComportamentoFactory getInstancia() {
		if (INSTANCIA == null) {
			INSTANCIA = new StaticComportamentoFactory();
			INSTANCIA.aposSetarPropriedades();
		}

		return INSTANCIA;
	}

	public static ComportamentoOrientadoADados getComportamento(TestCase testCase, CarregaDados carregaDados) {
		return getInstancia().CriaComportamento(testCase, carregaDados);
	}

	public StaticComportamentoFactory() {
	}

	public void aposSetarPropriedades() {
		try {
			this.propriedades = mergeProperties();
		} catch (IOException e) {
			logger.info(
					"Nenhum gerenciador-default.properties (valores default para todos os testes) no pacote default.");
		}
	}

	public ComportamentoOrientadoADados CriaComportamento(TestCase testCase, CarregaDados carregaDados) {
		TesteCaseInfo info = NomeTestCaseUtils.parseNomeTestCase(testCase.getName());

		if (info.eInstanciaDoMetodo()) {

			if (eMetodoComportamento(testCase, info, carregaDados)) {
				return new MetodoComportamentoOrientadoADados(testCase, info.getNomeDoMetodo(), carregaDados);
			}

			return new ComportamentoDoMetodoJUnit();
		}

		LinhaComportamentoOrientadoADados ComportamentoDaLinha = new LinhaComportamentoOrientadoADados(testCase,
				info.getNomeDoMetodo(), info.getIdDaLinha(), carregaDados);

		ComportamentoDaLinha.setProperties(this.propriedades);
		return ComportamentoDaLinha;
	}

	public boolean eMetodoComportamento(TestCase testCase, TesteCaseInfo info, CarregaDados carregaDados) {
		return eMetodoComportamento(testCase, info.getNomeDoMetodo(), carregaDados);
	}

	public boolean eMetodoComportamento(TestCase testCase, String nomeDoMetodo, CarregaDados carregaDados) {
		return carregaDados.carregarTabelas(testCase, nomeDoMetodo) != null;
	}
}

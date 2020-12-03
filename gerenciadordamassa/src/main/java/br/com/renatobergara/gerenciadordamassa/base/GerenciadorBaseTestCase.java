package br.com.renatobergara.gerenciadordamassa.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

import br.com.renatobergara.gerenciadordamassa.base.annotation.InjetaAutomatico;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.ComportamentoOrientadoADadosTestCaseImpl;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.GerenciamentoCachingDataSetLoader;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.cargadedados.CarregaDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.cargadedados.CarregaDadosImpl;

/**
 * Classe que deve ser estendida para implementar testes automatizados. Os
 * testes filhos possuem Comportamentos diferentes. Portanto essa classe
 * implementa as tarefas básicas e as subclasses definem quando fazê-las.
 * 
 */
public abstract class GerenciadorBaseTestCase extends ComportamentoOrientadoADadosTestCaseImpl {

	static {
		Locale.setDefault(new Locale("pt", "BR"));
	}

	protected final Logger logger = Logger.getLogger(this.getClass());

	protected GerenciadorBaseTestCase() {
		super();
		logger.trace("Criando " + this.getClass());
	}

	protected GerenciadorBaseTestCase(final String nomeCenario) {
		super();
		super.setName(nomeCenario);
	}

	@Override
	public void setUpAfterData() throws Exception {
		super.setUpAfterData();
		// Aqui abrigamos os testes que utilizarem a anotação 'InjetaAutomatico', a ter
		// o atributo 'nomeCenario' como
		// obrigatorio
		// Como os novos testes utilizarão essa anotação para fins de praticidade já
		// amarramos o obrigátoriedade do
		// atributo do cenário.
		if (this.getClass().isAnnotationPresent(InjetaAutomatico.class)) {
			Preconditions.checkNotNull(this.getNomeCenario(), "Propriedade 'nomeCenario' é obrigatória.");
		}

		logger.info("Nome do Cenário: " + this.getNomeCenario());

		if (this.getDescricaoCenarioCompleto() != null) {
			logger.warn("Propriedade 'descricaoCenarioCompleto' está nula.");
		} else {
			logger.info("Descrição do cenário: " + this.getDescricaoCenarioCompleto());
		}

		if (this.getDescricaoCenarioCompleto() != null) {
			logger.warn("Propriedade 'resultadoEsperado' está nula.");
		} else {
			logger.info("Resultado esperado do Cenário: " + this.getResultadoEsperado());
		}

	}

	@Override
	public void tearDownAfterData() throws Exception {
		super.tearDownAfterData();
	}

	@Override
	public CarregaDados criarCarregadorDeDados() {
		return new CarregaDadosImpl(GerenciamentoCachingDataSetLoader.of());
	}

	public String getDataHora() {
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
	}

}
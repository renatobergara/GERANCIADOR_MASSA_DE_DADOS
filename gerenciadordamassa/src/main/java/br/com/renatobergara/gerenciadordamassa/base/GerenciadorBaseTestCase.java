package br.com.renatobergara.gerenciadordamassa.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

import br.com.renatobergara.gerenciadordamassa.base.annotation.InjetaAutomatico;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.ComportamentoOrientadoADadosTestCaseImpl;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.cargadedados.CarregaDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.LinhaDeDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.TabelaDeDados;
import junit.framework.TestCase;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe que deve ser estendida para implementar testes automatizados. Os
 * testes filhos possuem Comportamentos diferentes. Portanto essa classe
 * implementa as tarefas básicas e as subclasses definem quando fazê-las.
 */
public abstract class GerenciadorBaseTestCase extends ComportamentoOrientadoADadosTestCaseImpl {

	public static final CarregaDados DUMMY_LOADER = new DummyLoader();

	static {
		Locale.setDefault(new Locale("pt", "BR"));
	}

	@Getter
	@Setter
	private String nomeCenario;

	@Getter
	@Setter
	private String descricaoCenarioCompleto;

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
			Preconditions.checkNotNull(this.nomeCenario, "Propriedade 'nomeCenario' é obrigatória.");
		}
	}

	@Override
	public CarregaDados criarCarregadorDeDados() {
		return DUMMY_LOADER;
	}

	/*
	 * Classe de loader que não faz nada. Utilizada para que possa estender um teste
	 * de DDSteps.
	 */
	private static class DummyLoader implements CarregaDados {

		@Override
		public TabelaDeDados carregarTabelas(TestCase paramTestCase, String paramString) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public LinhaDeDados carregarLinhas(TestCase testCase, String parametro1, String parametro2) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public String getDataHora() {
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
	}

}
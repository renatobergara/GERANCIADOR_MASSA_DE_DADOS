package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.comportamento;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.cargadedados.CarregaDados;
import junit.framework.TestCase;

public class ComportamentoOrientadoADadosBase {
	protected static final Logger logger = LoggerFactory.getLogger(LinhaComportamentoOrientadoADados.class);

	protected TestCase testeCase;

	protected String nomeDoMetodo;

	protected CarregaDados carregaDados;

	public ComportamentoOrientadoADadosBase(TestCase testeCase, String nomeDoMetodo, CarregaDados carregaDados) {
		Validate.notNull(testeCase, "Argumento testeCase não pode ser nulo.");
		Validate.notNull(nomeDoMetodo, "Argumento nomeDoMetodo não pode ser nulo.");
		Validate.notNull(carregaDados, "Argumento carregaDados não pode ser nulo.");
		this.testeCase = testeCase;
		this.nomeDoMetodo = nomeDoMetodo;
		this.carregaDados = carregaDados;
	}
}

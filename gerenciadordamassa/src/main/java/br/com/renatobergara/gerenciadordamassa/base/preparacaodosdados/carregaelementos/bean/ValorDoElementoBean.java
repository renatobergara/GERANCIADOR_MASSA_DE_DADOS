package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.bean;

import org.apache.commons.lang.Validate;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.ValorDosElementos;

public class ValorDoElementoBean implements ValorDosElementos {
	private final String nome;
	private final Object valor;

	public ValorDoElementoBean(String nome, Object valor) {
		Validate.notNull(nome);

		this.nome = nome;
		this.valor = valor;
	}

	@Override
	public String getNome() {
		return this.nome;
	}

	@Override
	public Object getValor() {
		return this.valor;
	}
}

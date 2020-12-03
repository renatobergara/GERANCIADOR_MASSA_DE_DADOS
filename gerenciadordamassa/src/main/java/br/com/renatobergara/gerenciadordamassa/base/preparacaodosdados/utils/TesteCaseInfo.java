package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.Getter;

public class TesteCaseInfo {
	@Getter
	private final String nomeDoMetodo;
	@Getter
	private final String idDaLinha;

	public TesteCaseInfo(String methodName, String rowId) {
		this.nomeDoMetodo = methodName;
		this.idDaLinha = rowId;
	}

	public TesteCaseInfo(String methodName) {
		this.nomeDoMetodo = methodName;
		this.idDaLinha = null;
	}

	public boolean eInstanciaDoMetodo() {
		return this.idDaLinha == null;
	}

	public boolean eInstanciaDaLinha() {
		return this.idDaLinha != null;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("NomeDoMetodo", this.nomeDoMetodo).append("idDaLinha", this.idDaLinha)
				.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

}

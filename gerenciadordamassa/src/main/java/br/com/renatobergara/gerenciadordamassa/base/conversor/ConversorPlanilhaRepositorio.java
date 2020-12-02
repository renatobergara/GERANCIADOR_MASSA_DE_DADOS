package br.com.renatobergara.gerenciadordamassa.base.conversor;

import br.com.renatobergara.gerenciadordamassa.base.xlsbeans.api.RepositorioPlanilhaFactory;
import lombok.Data;

/**
 * Converte os valores do campo Repositório
 */
@Data
public class ConversorPlanilhaRepositorio implements ConversorPlanilha<Object> {

	private final Package pacote;

	private final Class<?> classeTeste;

	@Override
	public Object converteValor(Class clazz, Object valor) {
		return RepositorioPlanilhaFactory.of().loadByBean(this.pacote, clazz, this.classeTeste)
				.findElementById(valor.toString());
	}

	@Override
	public Class<Object> getTipoClass() {
		return Object.class;
	}

}

package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;

import br.com.renatobergara.gerenciadordamassa.base.exception.DadosExcelException;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.GerenciamentoCachingDataSetLoader;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.bean.ValorDoElementoBean;

public class InicandoLinhaDeDados implements LinhaDeDados {

	protected final Logger logger = Logger.getLogger(GerenciamentoCachingDataSetLoader.class);

	private final List<ValorDosElementos> valores;
	private final String id;

	@SuppressWarnings("unchecked")
	public InicandoLinhaDeDados(LinhaDeDados linhaDeDados) {
		logger.debug("Criando linha de dados imutáveis");

		valores = values((Iterator<ValorDosElementos>) linhaDeDados.iterator());
		id = linhaDeDados.getId();
	}

	private List<ValorDosElementos> values(Iterator<ValorDosElementos> iterator) {
		com.google.common.collect.ImmutableList.Builder<ValorDosElementos> result = ImmutableList.builder();
		while (iterator.hasNext()) {
			ValorDosElementos object = iterator.next();
			try {
				result.add(new ValorDoElementoBean(object.getNome(), object.getValor()));
			} catch (DadosExcelException e) {
				e.printStackTrace();
			}
		}
		return result.build();
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public Iterator<ValorDosElementos> iterator() {
		return valores.iterator();
	}

}

package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.GerenciamentoCachingDataSetLoader;

public class IniciandoElementos implements Elementos {

	protected final Logger logger = Logger.getLogger(GerenciamentoCachingDataSetLoader.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 4874398918420625098L;
	private final Map<String, TabelaDeDados> dataTableByName;

	public IniciandoElementos(Elementos elementos) {
		logger.debug("Criando elementos imutáveis");
		String[] tableNames = elementos.getNomesTabelas();
		Builder<String, TabelaDeDados> builder = ImmutableMap.builder();
		for (int i = 0; i < tableNames.length; i++) {
			String tableName = tableNames[i];
			if (tableName.startsWith("test")) {
				builder.put(tableName, new IniciandoTabelaDeDados(elementos.getTabelas(tableName)));
			}
		}
		dataTableByName = builder.build();
		logger.debug("Criado elementos(tabelas e linha) imutáveis, para os metodos " + dataTableByName.keySet());
	}

	@Override
	public TabelaDeDados getTabelas(String nomeDaTabela) {
		return dataTableByName.get(nomeDaTabela);
	}

	@Override
	public String[] getNomesTabelas() {
		return this.dataTableByName.keySet().toArray(new String[] {});
	}

	@Override
	public Iterator<TabelaDeDados> iteratorTabelas() {
		return dataTableByName.values().iterator();
	}

}

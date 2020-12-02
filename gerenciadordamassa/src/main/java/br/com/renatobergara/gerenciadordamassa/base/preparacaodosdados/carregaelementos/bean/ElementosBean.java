package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.bean;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.Elementos;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.TabelaDeDados;

public class ElementosBean implements Elementos {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8334057874932980904L;

	protected final Map<String, TabelaDeDados> tabelas;

	public ElementosBean() {
		this.tabelas = new LinkedHashMap<String, TabelaDeDados>();
	}

	@Override
	public String[] getNomesTabelas() {
		return (String[]) this.tabelas.keySet().toArray(new String[this.tabelas.size()]);
	}

	@Override
	public Iterator<TabelaDeDados> iteratorTabelas() {
		return this.tabelas.values().iterator();
	}

	@Override
	public TabelaDeDados getTabelas(String tableName) {
		return (TabelaDeDados) this.tabelas.get(tableName);
	}

	public TabelaDeDados addTable(TabelaDeDados tabela) {
		Validate.notNull(tabela);
		String nome = tabela.getNome();
		Validate.notNull(nome);

		return (TabelaDeDados) this.tabelas.put(nome, tabela);
	}
}

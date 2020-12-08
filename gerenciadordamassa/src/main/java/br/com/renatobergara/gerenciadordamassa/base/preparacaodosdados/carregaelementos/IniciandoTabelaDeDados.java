package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.GerenciamentoCachingDataSetLoader;

public class IniciandoTabelaDeDados implements TabelaDeDados {

	protected final Logger logger = Logger.getLogger(GerenciamentoCachingDataSetLoader.class);

	private final Map<String, LinhaDeDados> dadosDaLinhaById;
	private final String name;

	public IniciandoTabelaDeDados(TabelaDeDados tabelaDeDados) {
		logger.debug("Criando tabelas de dados imutáveis");
		this.name = tabelaDeDados.getNome();
		Builder<String, LinhaDeDados> builder = ImmutableMap.builder();
		Iterator<LinhaDeDados> it = tabelaDeDados.iteratorLinhas();
		while (it.hasNext()) {
			LinhaDeDados dr = it.next();
			builder.put(dr.getId(), new InicandoLinhaDeDados(dr));
		}
		dadosDaLinhaById = builder.build();
	}

	@Override
	public LinhaDeDados EncontraLinhaPorId(String idDaLinha) {
		LinhaDeDados dr = dadosDaLinhaById.get(idDaLinha);
		Preconditions.checkNotNull(dr, "Não há linha de dados pro id " + idDaLinha + " na tabela " + name);
		return dr;
	}

	@Override
	public String getNome() {
		return this.name;
	}

	@Override
	public int getQuantidadeDeLinhas() {
		return dadosDaLinhaById.values().size();
	}

	@Override
	public Iterator<LinhaDeDados> iteratorLinhas() {
		return dadosDaLinhaById.values().iterator();
	}

}

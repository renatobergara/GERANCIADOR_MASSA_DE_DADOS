package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.CarregaElementos;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.Elementos;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.LinhaDeDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.TabelaDeDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.ValorDosElementos;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.bean.ElementosBean;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.bean.ValorDoElementoBean;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.excel.CarregaElementosExcel;

public class GerenciamentoCachingDataSetLoader implements CarregaElementos {

	private static final GerenciamentoCachingDataSetLoader instance = new GerenciamentoCachingDataSetLoader();

	protected final Logger logger = Logger.getLogger(GerenciamentoCachingDataSetLoader.class);

	protected static final ElementosBean NULL_DATASET = new ElementosBean();

	@Override
	public Elementos carregaElementos(Class<?> clazz) {
		return Caffeine.newBuilder().build(new CacheLoader<Class<?>, Elementos>() {
			@Override
			public Elementos load(Class<?> clazz) throws Exception {
				try {
					CarregaElementosExcel d = new CarregaElementosExcel();
					Elementos ds = d.carregaElementos(clazz);
					if (ds == null)
						return NULL_DATASET;
					return new IniciandoElementos(ds);
				} catch (RuntimeException e) {
					logger.error("Erro ao ler planilha da classe " + clazz, e);
					throw e;
				}
			}
		}).get(clazz);
	}

	public static GerenciamentoCachingDataSetLoader of() {
		return instance;
	}

	public class IniciandoElementos implements Elementos {
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

	public class IniciandoTabelaDeDados implements TabelaDeDados {

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

	public class InicandoLinhaDeDados implements LinhaDeDados {
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
				result.add(new ValorDoElementoBean(object.getNome(), object.getValor()));
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
}

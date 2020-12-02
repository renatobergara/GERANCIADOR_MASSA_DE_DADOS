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
					return new InitializedDataSet(ds);
				} catch (RuntimeException e) {
					logger.error("erro ao ler planilha da classe " + clazz, e);
					throw e;
				}
			}
		}).get(clazz);
	}

	public static GerenciamentoCachingDataSetLoader of() {
		return instance;
	}

	public class InitializedDataSet implements Elementos {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4874398918420625098L;
		private final Map<String, TabelaDeDados> dataTableByName;

		public InitializedDataSet(Elementos ds) {
			logger.debug("Criando dataset inicializado imutável");
			String[] tableNames = ds.getNomesTabelas();
			Builder<String, TabelaDeDados> builder = ImmutableMap.builder();
			for (int i = 0; i < tableNames.length; i++) {
				String tableName = tableNames[i];
				if (tableName.startsWith("test")) {
					builder.put(tableName, new InitializedDataTable(ds.getTabelas(tableName)));
				}
			}
			dataTableByName = builder.build();
			logger.debug("Criado dataset inicializado imutável, para os metodos " + dataTableByName.keySet());
		}

		@Override
		public TabelaDeDados getTabelas(String tableName) {
			return dataTableByName.get(tableName);
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

	public class InitializedDataTable implements TabelaDeDados {

		private final Map<String, LinhaDeDados> dataRowById;
		private final String name;

		public InitializedDataTable(TabelaDeDados dt) {
			this.name = dt.getNome();
			Builder<String, LinhaDeDados> builder = ImmutableMap.builder();
			Iterator<LinhaDeDados> it = dt.iteratorLinhas();
			while (it.hasNext()) {
				LinhaDeDados dr = it.next();
				builder.put(dr.getId(), new InitializedDataRow(dr));
			}
			dataRowById = builder.build();
		}

		@Override
		public LinhaDeDados EncontraLinhaPorId(String rowId) {
			LinhaDeDados dr = dataRowById.get(rowId);
			Preconditions.checkNotNull(dr, "Não há dr pro id " + rowId + " na tabela " + name);
			return dr;
		}

		@Override
		public String getNome() {
			return this.name;
		}

		@Override
		public int getQuantidadeDeLinhas() {
			return dataRowById.values().size();
		}

		@Override
		public Iterator<LinhaDeDados> iteratorLinhas() {
			return dataRowById.values().iterator();
		}

	}

	public class InitializedDataRow implements LinhaDeDados {
		private final List<ValorDosElementos> dataValues;
		private final String id;

		@SuppressWarnings("unchecked")
		public InitializedDataRow(LinhaDeDados dr) {
			dataValues = values((Iterator<ValorDosElementos>) dr.iterator());
			id = dr.getId();
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
			return dataValues.iterator();
		}
	}
}

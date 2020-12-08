package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados;

import org.apache.log4j.Logger;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.CarregaElementos;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.Elementos;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.IniciandoElementos;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.bean.ElementosBean;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.excel.CarregaElementosExcel;

public class GerenciamentoCachingDataSetLoader implements CarregaElementos {

	private static final GerenciamentoCachingDataSetLoader instance = new GerenciamentoCachingDataSetLoader();

	protected final Logger logger = Logger.getLogger(GerenciamentoCachingDataSetLoader.class);

	protected static final ElementosBean NULL_DATASET = new ElementosBean();

	public GerenciamentoCachingDataSetLoader() {
	}

	@Override
	public Elementos carregaElementos(Class<?> clazz) {
		return Caffeine.newBuilder().build(new CacheLoader<Class<?>, Elementos>() {
			@Override
			public Elementos load(Class<?> clazz) throws Exception {
				try {
					CarregaElementosExcel d = new CarregaElementosExcel();
					Elementos ds = d.carregaElementos(clazz);
					if (ds == null) {
						return NULL_DATASET;
					}
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

}

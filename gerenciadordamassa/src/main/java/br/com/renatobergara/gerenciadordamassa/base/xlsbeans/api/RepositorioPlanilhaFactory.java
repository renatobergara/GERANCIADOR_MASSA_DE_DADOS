package br.com.renatobergara.gerenciadordamassa.base.xlsbeans.api;

import java.io.InputStream;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;

import br.com.renatobergara.gerenciadordamassa.base.exception.GerenciadorTestException;
import lombok.Data;
import net.java.amateras.xlsbeans.XLSBeansException;

/**
 * Classe responsável por Fornecer Repositórios e Armazena-los em Cache.
 */
public final class RepositorioPlanilhaFactory {

	private static RepositorioPlanilhaFactory instance = new RepositorioPlanilhaFactory();

	private RepositorioPlanilhaFactory() {
		// Não deve instanciar essa classe
	}

	public static RepositorioPlanilhaFactory of() {
		return instance;
	}

	private <P extends RepositorioPlanilha<?>> RepositorioPlanilha<P> loadRepositorioByBean(InputStream xlsIn,
			final Package pacote, final Class<P> beanClazz, Class<?> classeTeste) {
		try {
			return GerenciadorXlsBeans.loadRepositorioByBean(pacote, beanClazz, classeTeste).loadRepositorio(xlsIn);
		} catch (XLSBeansException e) {
			throw new GerenciadorTestException(e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <P extends RepositorioPlanilha<?>> P loadByBean(Package pacotePlanilha, final Class clazz) {
		return (P) Caffeine.newBuilder().softValues().build(new CacheLoader<ChaveRepositorio, RepositorioPlanilha>() {
			@Override
			public RepositorioPlanilha load(final ChaveRepositorio from) throws Exception {
				return loadRepositorioByBean(
						LocalizadorPlanilha.getPlanilha(from.pacoteDaPlanilha, from.classeDoBean, from.classeTeste),
						from.pacoteDaPlanilha, from.classeDoBean, from.classeTeste);
			}
		}).get(new ChaveRepositorio(pacotePlanilha, clazz, null));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <P extends RepositorioPlanilha<?>> P loadByBean(Package pacotePlanilha, final Class clazz,
			final Class<?> classeTeste) {
		return (P) Caffeine.newBuilder().softValues().build(new CacheLoader<ChaveRepositorio, RepositorioPlanilha>() {
			@Override
			public RepositorioPlanilha load(final ChaveRepositorio from) throws Exception {
				return loadRepositorioByBean(
						LocalizadorPlanilha.getPlanilha(from.pacoteDaPlanilha, from.classeDoBean, from.classeTeste),
						from.pacoteDaPlanilha, from.classeDoBean, from.classeTeste);
			}
		}).get(new ChaveRepositorio(pacotePlanilha, clazz, classeTeste));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <P extends RepositorioPlanilha<?>> P loadByBean(final Class clazz, final Class<?> classeTeste) {
		return (P) Caffeine.newBuilder().softValues().build(new CacheLoader<ChaveRepositorio, RepositorioPlanilha>() {
			@Override
			public RepositorioPlanilha load(final ChaveRepositorio from) throws Exception {
				return loadRepositorioByBean(
						LocalizadorPlanilha.getPlanilha(from.pacoteDaPlanilha, from.classeDoBean, from.classeTeste),
						from.pacoteDaPlanilha, from.classeDoBean, from.classeTeste);
			}
		}).get(new ChaveRepositorio(classeTeste.getPackage(), clazz, classeTeste));
	}

	@Data
	@SuppressWarnings("rawtypes")
	private class ChaveRepositorio {
		final Package pacoteDaPlanilha;
		final Class classeDoBean;
		final Class classeTeste;
	}

}

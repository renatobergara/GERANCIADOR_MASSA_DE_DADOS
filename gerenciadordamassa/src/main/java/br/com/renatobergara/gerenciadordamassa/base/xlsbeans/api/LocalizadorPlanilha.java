package br.com.renatobergara.gerenciadordamassa.base.xlsbeans.api;

import java.io.InputStream;

import com.google.common.base.Preconditions;

/**
 * Responsável por Localizar a Planilha do Repositório.
 */
public class LocalizadorPlanilha {

	/**
	 * Devolve a planilha do repositorio associado ao beanClass.
	 */
	public static InputStream getPlanilha(Package pacoteDaPlanilha, Class<?> beanClass, Class<?> classeTeste) {
		if (classeTeste != null) {
			return carregaPlanilha(getNomePlanilhaTeste(classeTeste));
		}
		// Buscamos a Planilha no Pacote escolhido.
		return carregaPlanilha(getNomePlanilhaRepositorio(pacoteDaPlanilha, beanClass));
	}

	private static InputStream carregaPlanilha(String nomePlanilha) {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(nomePlanilha);
		Preconditions.checkNotNull(inputStream, "Não foi possivel achar a Planilha de Repositório referente ao Bean '"
				+ nomePlanilha
				+ "'. \n Obs: Mapeamentos utilizando @XBManyToOne e @XBOneToONe requerem uma planilha referente ao tipo, seguindo o padrão RepositorioNomeTipo.xls, caso o mapeamento for para uma tabela da própria planilha utilizar no setter do campo a anotação @XBComponentsRecords."
				+ "\n Verifique se sua planilha segue a convenção de nomenclaturas, começando com Repositório e tendo a extensão .xls.");
		return inputStream;
	}

	public static String getNomePlanilhaRepositorio(Package pacoteDaPlanilha, Class<?> clazz) {
		return (pacoteDaPlanilha.getName() + ".Repositorio" + clazz.getSimpleName()).replace(".", "/") + ".xls";
	}

	public static String getNomePlanilhaTeste(Class<?> classeTeste) {
		return classeTeste.getName().replace(".", "/") + ".xls";
	}
}

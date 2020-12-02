package br.com.renatobergara.gerenciadordamassa.base.xlsbeans.api;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Predicate;

/**
 * Repositório dos registros da Tabela da Planilha.
 * 
 * @param <T> - Tipo do Elemtento do Repositório.
 */
public interface RepositorioPlanilha<T> {

	/**
	 * Possue todos os elementos da planilha;
	 */
	public List<T> getElementos();

	/**
	 * Class dos elementos da planilha;
	 */
	public Class<T> getTipo();

	/**
	 * Class do Teste unitario;
	 */
	public Class<?> getClasseTeste();

	/**
	 * Pacote de onde se encontra a Planilha do Repositório
	 */
	public Package getPacotePlanilha();

	/**
	 * Para buscar um elemento do repositório pelo Identificador.
	 */
	public T findElementById(Serializable Id);

	/**
	 * Para buscar n elementos do repositório, dado n Identificadores.
	 */
	public List<T> findElementsByIds(List<Serializable> Ids);

	/**
	 * Para buscar n elementos do repositório, dado um predicado.
	 */
	public List<T> findElementsByPredicate(Predicate<T> predicate);

}

package br.com.renatobergara.gerenciadordamassa.base.conversor;

/**
 * Responsavel por fornecer a conversão dos Tipos Básicos do Teste, utilizados
 * em Testes Unitários e Funcioanis.
 * 
 * @param <T> - Tipo a ser Convertido
 */
public interface ConversorPlanilha<T> {

	/**
	 * Responsável por fazer a conversão de String para o Tipo Especifico.
	 * 
	 * @param clazz - Classe ou qualquer Subclasse do tipo a ser convertido.
	 * @param valor - Valor do Tipo
	 * 
	 * @return Tipo Convertido
	 * @throws Exception - Qualquer problema encontrado para fazer a conversão.
	 */
	public T converteValor(Class<? extends T> clazz, Object valor) throws Exception;

	/**
	 * Classe da Conversão.
	 * 
	 * @return - Classe de Conversão
	 */
	public Class<T> getTipoClass();

}

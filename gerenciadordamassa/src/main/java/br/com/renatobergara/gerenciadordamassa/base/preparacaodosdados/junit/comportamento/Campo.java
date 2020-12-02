package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.comportamento;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import br.com.renatobergara.gerenciadordamassa.base.conversor.ConversorPlanilha;
import br.com.renatobergara.gerenciadordamassa.base.conversor.ConversorUtils;
import br.com.renatobergara.gerenciadordamassa.base.exception.GerenciadorTestException;
import lombok.Data;

/**
 * Manten as informações do campo da planilha.
 */
@Data
class Campo {

	/**
	 * Nome da coluna que representa o atributo da classe de teste.
	 */
	private final String propriedade;

	/**
	 * Valor a ser atribuido ao atributo da classe.
	 */
	private final Object valor;

	/**
	 * Caso for um tipo mock, esse será o nome do método a ser mockado.
	 */
	private final String metodo;

	/**
	 * Argumentos do Método que será mockado.
	 */
	private final String[] args;

	/**
	 * Classe do atributo da classe de teste OU Classe do método que será mockado.
	 */
	private final Class<?> classeCampo;

	/**
	 * Classe do Teste Unitário.
	 */
	private final Class<?> classeTeste;

	/**
	 * Conversor especifico do campo.
	 */
	private final ConversorPlanilha<?> conversor;

	String getNomeMetodo() {
		return (this.metodo != null) ? this.metodo.substring(this.metodo.indexOf(".") + 1, this.metodo.length()) : null;
	}

	// Precisamos verificar se as Strings do array existe mais de um modelo no
	// linha.
	private List<Object> trataValoresComMaisDeUmModeloNaMesmaLinha() {
		String[] valores = this.getValor().toString().split(";");
		List<Object> resultado = Lists.newArrayList();
		for (String v : valores) {
			resultado.add(v);
		}
		return resultado;
	}

	Object getValorConvertido() {
		if (this.classeCampo.isAssignableFrom(List.class) || this.classeCampo.isAssignableFrom(Set.class)) {
			Class<?> generica = ReflectionUtils.getClassByField(this.getPropriedade(), this.classeTeste);
			final List<Object> valores = this.trataValoresComMaisDeUmModeloNaMesmaLinha();
			Collection<Object> resultado = this.classeCampo.isAssignableFrom(List.class) ? Lists.newArrayList()
					: Sets.newHashSet();
			for (Object v : valores) {
				resultado.add(this.converte(generica, v));
			}
			return resultado;
		}
		return this.converte(this.classeCampo, this.getValor());
	}

	/**
	 * Cria um campo contendo todas as informações da Celula da Planilha, com um
	 * conversor especifico.
	 */
	static Campo of(final String coluna, final Class<?> classeTeste, final Object valor,
			ConversorPlanilha<?> conversor) {
		String[] propriedades = coluna.split("\\.");
		String metodo = null;
		String[] args = new String[] {};
		if (propriedades.length > 1) {
			String[] ultimoMetodo = coluna.split(":");
			if (ultimoMetodo.length > 1) {
				args = ultimoMetodo[1].split(",");
			}
			metodo = ultimoMetodo[0];
		}
		Class<?> classeCampo = ReflectionUtils
				.getClassByNestedProprerties(classeTeste, (metodo != null ? metodo : propriedades[0]), args)
				.getReturnType();
		return new Campo(propriedades[0], valor, metodo, args, classeCampo, classeTeste, conversor);
	}

	/**
	 * Cria um campo contendo todas as informações da Celula da Planilha, utiliza
	 * para conversão dos tipos os Conversores padrão.
	 */
	static Campo of(final String coluna, final Class<?> classeTeste, final Object valor) {
		return of(coluna, classeTeste, valor, null);
	}

	@SuppressWarnings("unchecked")
	private Object converte(Class clazz, Object valor) {
		ConversorPlanilha<?> conversorPlanilha = (this.conversor != null) ? this.conversor
				: ConversorUtils.getConversor(clazz);
		Object valorConvertido = null;
		try {
			valorConvertido = conversorPlanilha.converteValor(clazz, valor);
		} catch (Exception e) {
			StringBuilder erro = new StringBuilder();
			erro.append("Falha na conversão do Tipo da Classe: '" + clazz + "', utilizando o valor '" + valor.toString()
					+ "' para setar o campo '" + this.propriedade + "'.");
			erro.append("\n Possiveis causas são: ");
			erro.append(
					"\n Se o Tipo for um Repositório, verificar se a coluna do header segue o padrão '< >'(Repositório da Planilha de Teste) , '<< >>'(Repositório Local, Planilha no mesmo pacote do teste) ou '<<< >>>'(Repositório Global, Planilha no pacote diferente do teste); ");
			erro.append("\n Se o Tipo for um Model/Entidade, verificar se a coluna do header segue o padrão '[ ]'; ");
			erro.append(
					"\n Caso não for nenhum desses Tipos, verificar se existe um conversor para essa classe e se ele está registrado em algum local seguindo o exemplo: ");
			erro.append(
					"\n registraConversorTipos(NomeDaClasse.class, new ConversorPlanilhaNomeDaClasse()). Verificar mais detalhes --> ");
			throw new GerenciadorTestException(erro.toString() + e.getMessage());
		}
		return valorConvertido;
	}

}
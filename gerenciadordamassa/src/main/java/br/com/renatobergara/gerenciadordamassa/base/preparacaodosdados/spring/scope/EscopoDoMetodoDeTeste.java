package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.spring.scope;

import org.springframework.beans.factory.config.Scope;

import br.com.renatobergara.gerenciadordamassa.base.exception.GerenciadorIllegalStateException;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.utils.StringUtils;

public class EscopoDoMetodoDeTeste extends DelegaScopeThreadLocalSupport {
	private static ThreadLocal<Scope> currentScope = new ThreadLocal<Scope>();

	private static EscopoDoMetodoDeTeste INSTANCE = new EscopoDoMetodoDeTeste();

	public static void iniciaMetodoDeTeste(String nome) {
		INSTANCE.enterScope(new InMemoryScope(nome));
	}

	public static void terminaMetodoDoTeste(String nome) {
		if (!StringUtils.equals(nome, INSTANCE.getScopeOrThrow().getConversationId())) {
			throw new GerenciadorIllegalStateException(
					"O escopo não corresponde ao nome do método de teste '" + nome + "'.");
		}

		INSTANCE.exitScope();
	}

	@Override
	protected ThreadLocal<Scope> getScopeThreadLocal() {
		return (ThreadLocal<Scope>) currentScope;
	}

	@Override
	public Object resolveContextualObject(String key) {
		return null;
	}
}

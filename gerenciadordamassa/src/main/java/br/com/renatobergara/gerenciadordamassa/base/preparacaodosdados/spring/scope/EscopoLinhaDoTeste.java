package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.spring.scope;

import org.springframework.beans.factory.config.Scope;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.utils.StringUtils;

public class EscopoLinhaDoTeste extends DelegaScopeThreadLocalSupport {
	private static ThreadLocal<Scope> currentScope = new ThreadLocal<Scope>();

	protected static final EscopoLinhaDoTeste INSTANCE = new EscopoLinhaDoTeste();

	public static void iniciaALinhaDoTeste(String nome) {
		INSTANCE.enterScope(new InMemoryScope(nome));
	}

	public static void terminaALinhaDoTeste(String nome) {
		if (!StringUtils.equals(nome, INSTANCE.getScopeOrThrow().getConversationId())) {
			throw new IllegalStateException("O escopo não corresponde ao nome da linha de teste '" + nome + "'.");
		}

		INSTANCE.exitScope();
	}

	@Override
	protected ThreadLocal<Scope> getScopeThreadLocal() {
		return currentScope;
	}

	@Override
	public Object resolveContextualObject(String key) {
		return null;
	}
}

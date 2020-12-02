package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.spring.scope;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import br.com.renatobergara.gerenciadordamassa.base.exception.GerenciadorIllegalStateException;
import br.com.renatobergara.gerenciadordamassa.base.exception.GerenciadorTestException;

public abstract class DelegaScopeThreadLocalSupport implements Scope {

	protected Scope getScopeOrThrow() {
		Scope escopo = getScope();
		if (escopo == null) {
			throw new IllegalStateException("Sem Escopo Local.");
		}
		return escopo;
	}

	protected void enterScope(Scope scope) {
		if (getScope() != null) {
			throw new GerenciadorIllegalStateException(
					"Não é possível encontrar novo escopo '" + scope.getConversationId()
							+ "' uma vez que já estamos no escopo '" + getScope().getConversationId() + "'");
		}

		getScopeThreadLocal().set(scope);
	}

	protected void exitScope() {
		Scope scope = getScopeOrThrow();

		getScopeThreadLocal().set(null);

		if ((scope instanceof DisposableBean)) {
			DisposableBean disposableScope = (DisposableBean) scope;
			try {
				disposableScope.destroy();
			} catch (Exception e) {
				throw new GerenciadorTestException("Exception ao destruir escopo'" + scope.getConversationId() + "'.");
			}
		}
	}

	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		return getScopeOrThrow().get(name, objectFactory);
	}

	@Override
	public String getConversationId() {
		return getScopeOrThrow().getConversationId();
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback) {
		getScopeOrThrow().registerDestructionCallback(name, callback);
	}

	@Override
	public Object remove(String name) {
		return getScopeOrThrow().remove(name);
	}

	protected Scope getScope() {
		return (Scope) getScopeThreadLocal().get();
	}

	protected abstract ThreadLocal<Scope> getScopeThreadLocal();
}

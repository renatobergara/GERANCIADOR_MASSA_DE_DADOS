package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.spring.scope;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

public class InMemoryScope implements Scope, DisposableBean {
	protected Map<String, ReferenciaScopeBean> beanRefs = new HashMap<String, ReferenciaScopeBean>();
	protected String conversationId;

	public InMemoryScope(String conversationId) {
		this.conversationId = conversationId;
	}

	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		ReferenciaScopeBean ref = (ReferenciaScopeBean) this.beanRefs.get(name);

		if (ref == null) {
			Object bean = objectFactory.getObject();
			ref = new ReferenciaScopeBean(bean);

			this.beanRefs.put(name, ref);
		}

		return ref.getBean();
	}

	@Override
	public String getConversationId() {
		return this.conversationId;
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback) {
		ReferenciaScopeBean ref = (ReferenciaScopeBean) this.beanRefs.get(name);

		if (ref == null) {
			throw new IllegalArgumentException(
					"Nenhum bean nomeado '" + name + "' no escopo '" + this.conversationId + ".");
		}

		ref.registerDestructionCallback(callback);
	}

	@Override
	public Object remove(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void destroy() throws Exception {
		Iterator<String> iterator = this.beanRefs.keySet().iterator();
		while (iterator.hasNext()) {
			String name = (String) iterator.next();

			ReferenciaScopeBean ref = (ReferenciaScopeBean) this.beanRefs.remove(name);

			if (ref != null) {
				ref.destroy();
			}
		}
	}

	@Override
	public Object resolveContextualObject(String key) {
		return null;
	}
}

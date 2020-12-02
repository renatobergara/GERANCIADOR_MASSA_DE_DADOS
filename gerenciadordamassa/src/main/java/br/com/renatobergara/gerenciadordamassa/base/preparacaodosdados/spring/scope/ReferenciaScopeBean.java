package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.spring.scope;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.DisposableBean;

public class ReferenciaScopeBean implements DisposableBean {
	protected Object bean;
	protected List<Object> destructionCallbacks;

	public ReferenciaScopeBean(Object bean) {
		this.bean = bean;
	}

	public void registerDestructionCallback(Runnable callback) {
		if (this.destructionCallbacks == null) {
			this.destructionCallbacks = new java.util.LinkedList<>();
		}

		this.destructionCallbacks.add(callback);
	}

	@Override
	public void destroy() throws Exception {
		if (this.destructionCallbacks != null) {
			Iterator<Object> iterator = this.destructionCallbacks.iterator();
			while (iterator.hasNext()) {
				Runnable runnable = (Runnable) iterator.next();
				runnable.run();
			}
		}

		if ((this.bean instanceof DisposableBean)) {
			DisposableBean disposableBean = (DisposableBean) this.bean;
			disposableBean.destroy();
		}
	}

	public Object getBean() {
		return this.bean;
	}
}

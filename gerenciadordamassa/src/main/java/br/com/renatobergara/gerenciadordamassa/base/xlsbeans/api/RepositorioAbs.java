package br.com.renatobergara.gerenciadordamassa.base.xlsbeans.api;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;

import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBIdentifier;
import br.com.renatobergara.gerenciadordamassa.base.util.clazz.FieldTestUtils;
import br.com.renatobergara.gerenciadordamassa.base.util.clazz.FieldTestUtils.FieldDescriptor;

/**
 * Classe para sobrescrever o método findById comun entre todos os Repositórios.
 */
public abstract class RepositorioAbs<T> implements RepositorioPlanilha<T> {

	@Override
	public T findElementById(Serializable id) {
		for (T object : this.getElementos()) {
			FieldDescriptor descriptor = FieldTestUtils.getFieldDescriptorByAnnotation(object.getClass(),
					XBIdentifier.class);
			Preconditions.checkNotNull(descriptor, "Não foi encontrado o campo com a anotação @XBIdentifier");
			if (descriptor.getValue(object).equals(id)) {
				return object;
			}
		}
		return null;
	}

	@Override
	public List<T> findElementsByIds(List<Serializable> Ids) {
		Builder<T> elementos = ImmutableList.builder();
		for (Serializable id : Ids) {
			elementos.add(this.findElementById(id));
		}
		return elementos.build();
	}

	@Override
	public List<T> findElementsByPredicate(Predicate<T> predicate) {
		return Lists.newArrayList(Collections2.filter(this.getElementos(), predicate));
	}

}

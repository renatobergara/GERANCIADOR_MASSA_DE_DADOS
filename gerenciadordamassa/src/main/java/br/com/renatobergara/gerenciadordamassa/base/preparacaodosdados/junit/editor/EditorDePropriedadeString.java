package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.junit.editor;

import java.beans.PropertyEditorSupport;

public class EditorDePropriedadeString extends PropertyEditorSupport {
	public EditorDePropriedadeString() {
	}

	public EditorDePropriedadeString(Object source) {
		super(source);
	}

	@Override
	public void setValue(Object valor) {
		if ((valor != null) && (!(valor instanceof String))) {
			valor = valor.toString();
		}

		super.setValue(valor);
	}

	@Override
	public void setAsText(String texto) throws IllegalArgumentException {
		setValue(texto);
	}
}

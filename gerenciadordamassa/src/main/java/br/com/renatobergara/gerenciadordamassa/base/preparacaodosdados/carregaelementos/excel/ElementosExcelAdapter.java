package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.excel;

import java.util.Iterator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.Elementos;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.TabelaDeDados;
import jxl.Sheet;
import jxl.Workbook;

class ElementosExcelAdapter implements Elementos {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8413735108691622385L;
	final Workbook workbook;

	public ElementosExcelAdapter(Workbook workbook) {
		Validate.notNull(workbook, "O Argumento workbook não pode ser nulo.");
		this.workbook = workbook;
	}

	@Override
	public String[] getNomesTabelas() {
		String[] sheetNames = this.workbook.getSheetNames();

		if (sheetNames == null) {
			sheetNames = ArrayUtils.EMPTY_STRING_ARRAY;
		}

		return sheetNames;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Iterator iteratorTabelas() {
		return new Iterator<Object>() {

			private int current = -1;

			private final int last = ElementosExcelAdapter.this.workbook.getNumberOfSheets() - 1;

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Não é possível remover uma sheet de um arquivo Excel.");
			}

			@Override
			public boolean hasNext() {
				return this.current < this.last;
			}

			@Override
			public Object next() {
				if (hasNext()) {
					this.current += 1;
					Sheet sheet = ElementosExcelAdapter.this.workbook.getSheet(this.current);
					return ElementosExcelAdapter.this.createDataTableAdapter(sheet);
				}
				return null;
			}
		};
	}

	@Override
	public TabelaDeDados getTabelas(String tableName) {
		Sheet sheet = this.workbook.getSheet(tableName);

		if (sheet == null) {
			return null;
		}

		return createDataTableAdapter(sheet);
	}

	protected TabelaDeDados createDataTableAdapter(Sheet sheet) {
		return new DadosDaTabelaExcelAdapter(sheet);
	}
}

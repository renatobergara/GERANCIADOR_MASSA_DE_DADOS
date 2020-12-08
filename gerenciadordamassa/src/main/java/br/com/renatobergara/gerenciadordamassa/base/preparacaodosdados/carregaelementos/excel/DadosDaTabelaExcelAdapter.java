package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.excel;

import java.util.Iterator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.iterators.FilterIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import br.com.renatobergara.gerenciadordamassa.base.exception.DadosExcelException;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.LinhaDeDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.TabelaDeDados;
import jxl.Cell;
import jxl.Sheet;

class DadosDaTabelaExcelAdapter implements TabelaDeDados {
	public static final String DELIMITADOR = "---";
	final int primeirosElementos;
	final int ultimosElementos;
	final int primeiroCabecalho;
	final int ultimoCabecalho;
	final Sheet sheet;

	class CellToStringTransformer implements Transformer {
		CellToStringTransformer() {
		}

		@Override
		public Object transform(Object arg0) {
			Cell cell = (Cell) arg0;
			return cell.getContents();
		}
	}

	class LinhasIterator implements Iterator<Object> {
		private int current = DadosDaTabelaExcelAdapter.this.primeirosElementos - 1;

		LinhasIterator() {
		}

		@Override
		public boolean hasNext() {
			return this.current < DadosDaTabelaExcelAdapter.this.ultimosElementos;
		}

		@Override
		public Object next() {
			this.current += 1;
			return DadosDaTabelaExcelAdapter.this.CriarLinhaDeDadosAdapter(DadosDaTabelaExcelAdapter.this,
					this.current);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Não é possível remover linhas.");
		}
	}

	private static int encontraDelimitador(Cell[] colunas, int i) {
		for (int j = i; j < colunas.length; j++) {
			Cell cell = colunas[j];
			if (StringUtils.equals("---", cell.getContents())) {
				return cell.getRow();
			}
		}

		return -1;
	}

	public DadosDaTabelaExcelAdapter(Sheet sheet) throws DadosExcelException {
		this.sheet = sheet;

		Cell[] colunas = sheet.getColumn(0);

		int primeiroDelimitador = encontraDelimitador(colunas, 0);

		if (primeiroDelimitador < 0) {
			throw new DadosExcelException(
					"Não foi possível encontrar o delimitador inicial na coluna A. Coloque uma célula com '--- antes dos cabeçalhos.");
		}

		this.primeiroCabecalho = (primeiroDelimitador + 1);

		int segundoDelimitador = encontraDelimitador(colunas, primeiroDelimitador + 1);

		if (segundoDelimitador < 0) {
			throw new DadosExcelException(
					"Não foi possível encontrar o delimitador entre os cabeçalhos e os dados na coluna A. Coloque uma célula com '--- após os cabeçalhos.");
		}

		this.ultimoCabecalho = (segundoDelimitador - 1);
		this.primeirosElementos = (segundoDelimitador + 1);

		int terceiroDelimitador = encontraDelimitador(colunas, segundoDelimitador + 1);

		if (terceiroDelimitador < 0) {
			throw new DadosExcelException(
					"Não foi possível encontrar o delimitador após os dados na coluna A. Coloque uma célula com '--- após os dados.");
		}

		this.ultimosElementos = (terceiroDelimitador - 1);
	}

	public int getquantidadeDeCabecalho() {
		return CollectionUtils.size(headerCellsIterator());
	}

	@Override
	public int getQuantidadeDeLinhas() {
		return this.ultimosElementos - this.primeirosElementos + 1;
	}

	public Sheet getSheet() {
		return this.sheet;
	}

	public Iterator<?> iteratorCabecalho() {
		return new TransformIterator(headerCellsIterator(), new CellToStringTransformer());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Iterator iteratorLinhas() {
		return new LinhasIterator();
	}

	protected LinhaDeDados CriarLinhaDeDadosAdapter(DadosDaTabelaExcelAdapter table, int row) {
		return new DadosDaLinhaExcelAdapter(table, row);
	}

	Iterator<?> headerCellsIterator() {
		IteratorChain iteratorChain = new IteratorChain();

		for (int i = this.primeiroCabecalho; i <= this.ultimoCabecalho; i++) {
			iteratorChain.addIterator(new ObjectArrayIterator(this.sheet.getRow(i)));
		}

		Iterator<?> headerIter = new FilterIterator(iteratorChain, CabecalhoPredicate.INSTANCE);

		return headerIter;
	}

	@Override
	public String getNome() {
		return this.sheet.getName();
	}

	@Override
	public LinhaDeDados EncontraLinhaPorId(String rowId) {
		Validate.notEmpty(rowId, "O argumento rowId não deve ser uma string vazia.");

		String NumeroDaLinha = StringUtils.stripStart(rowId, "Linha Excel ");

		int LinhaIndex = Integer.parseInt(NumeroDaLinha) - 1;

		return CriarLinhaDeDadosAdapter(this, LinhaIndex);
	}
}

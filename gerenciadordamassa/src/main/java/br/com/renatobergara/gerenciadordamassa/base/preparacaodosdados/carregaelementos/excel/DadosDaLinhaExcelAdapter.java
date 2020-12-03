package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.excel;

import java.util.Iterator;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.iterators.FilterIterator;
import org.apache.commons.collections4.iterators.TransformIterator;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.LinhaDeDados;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.ValorDosElementos;
import jxl.Cell;
import jxl.CellType;

class DadosDaLinhaExcelAdapter implements LinhaDeDados {
	final DadosDaTabelaExcelAdapter tabela;
	final int linha;

	class CellNotEmptyPredicate implements Predicate {
		CellNotEmptyPredicate() {
		}

		@Override
		public boolean evaluate(Object object) {
			Cell header = (Cell) object;

			Cell cell = DadosDaLinhaExcelAdapter.this.tabela.getSheet().getCell(header.getColumn(),
					DadosDaLinhaExcelAdapter.this.linha);

			if ((cell == null) || (cell.getType() == CellType.EMPTY)) {
				return false;
			}

			return true;
		}
	}

	class HeaderToCellTransformer implements Transformer {
		HeaderToCellTransformer() {
		}

		@Override
		public Object transform(Object object) {
			Cell header = (Cell) object;

			Cell cell = DadosDaLinhaExcelAdapter.this.tabela.getSheet().getCell(header.getColumn(),
					DadosDaLinhaExcelAdapter.this.linha);

			return DadosDaLinhaExcelAdapter.this.CriarValorDosElementosAdapter(header, cell);
		}
	}

	public DadosDaLinhaExcelAdapter(DadosDaTabelaExcelAdapter tabela, int linha) {
		this.linha = linha;
		this.tabela = tabela;
	}

	@Override
	public String getId() {
		return "Linha do Excel " + (this.linha + 1);
	}

	@Override
	public Iterator<?> iterator() {
		FilterIterator nonEmptyHeadersIter = new FilterIterator(this.tabela.headerCellsIterator(),
				new CellNotEmptyPredicate());

		TransformIterator valorDosDadosIter = new TransformIterator(nonEmptyHeadersIter, new HeaderToCellTransformer());

		return valorDosDadosIter;
	}

	protected ValorDosElementos CriarValorDosElementosAdapter(Cell header, Cell cell) {
		return new ValorDosElementosExcelAdapter(header, cell);
	}
}
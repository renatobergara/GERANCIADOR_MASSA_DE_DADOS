package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.excel;

import br.com.renatobergara.gerenciadordamassa.base.exception.DadosExcelException;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.ValorDosElementos;
import jxl.BooleanCell;
import jxl.Cell;
import jxl.CellReferenceHelper;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;

class ValorDosElementosExcelAdapter implements ValorDosElementos {
	private final Cell header;
	private final Cell cell;

	public ValorDosElementosExcelAdapter(Cell header, Cell cell) {
		this.header = header;
		this.cell = cell;
	}

	@Override
	public String getNome() {
		return this.header.getContents();
	}

	@Override
	public Object getValor() throws DadosExcelException {
		if (this.cell == null) {
			return null;
		}
		CellType type = this.cell.getType();

		if ((type == CellType.ERROR) || (type == CellType.FORMULA_ERROR)) {
			throw new DadosExcelException("Erro na planilha do excel na célula "
					+ CellReferenceHelper.getCellReference(this.cell.getColumn(), this.cell.getRow()));
		}
		if (type == CellType.EMPTY) {
			return null;
		}
		if ((type == CellType.LABEL) || (type == CellType.STRING_FORMULA)) {
			return this.cell.getContents();
		}
		if ((type == CellType.BOOLEAN) || (type == CellType.BOOLEAN_FORMULA)) {
			BooleanCell boolCell = (BooleanCell) this.cell;
			return Boolean.valueOf(boolCell.getValue());
		}
		if ((type == CellType.NUMBER) || (type == CellType.NUMBER_FORMULA)) {
			NumberCell numberCell = (NumberCell) this.cell;
			return new Double(numberCell.getValue());
		}
		if ((type == CellType.DATE) || (type == CellType.DATE_FORMULA)) {
			DateCell dateCell = (DateCell) this.cell;
			return dateCell.getDate();
		}

		throw new DadosExcelException("Dados desconhecidos na planilha do excel na célula "
				+ CellReferenceHelper.getCellReference(this.cell.getColumn(), this.cell.getRow()));
	}
}

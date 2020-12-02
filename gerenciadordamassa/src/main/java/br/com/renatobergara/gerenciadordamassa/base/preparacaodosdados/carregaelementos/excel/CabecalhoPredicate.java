package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.excel;

import jxl.Cell;
import jxl.CellReferenceHelper;
import jxl.CellType;

class CabecalhoPredicate implements net.sf.cglib.core.Predicate, org.apache.commons.collections.Predicate {
	public static final CabecalhoPredicate INSTANCE = new CabecalhoPredicate();

	@Override
	public boolean evaluate(Object objecto) {
		Cell cell = (Cell) objecto;

		if (cell == null) {
			return false;
		}
		CellType type = cell.getType();

		if ((type == CellType.LABEL) || (type == CellType.STRING_FORMULA))
			return true;
		if (type == CellType.EMPTY) {
			return false;
		}

		throw new DadosExcelException("Tipo de célula inválido para o cabeçalho: "
				+ CellReferenceHelper.getCellReference(cell.getColumn(), cell.getRow()));
	}
}

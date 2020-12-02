package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.excel;

public class DadosExcelException extends RuntimeException {
	private static final long serialVersionUID = 3979265828812436023L;

	public DadosExcelException() {
	}

	public DadosExcelException(String message) {
		super(message);
	}

	public DadosExcelException(Throwable cause) {
		super(cause);
	}

	public DadosExcelException(String message, Throwable cause) {
		super(message, cause);
	}
}

package br.com.renatobergara.gerenciadordamassa.base.exception;

public class GerenciadorIllegalStateException extends IllegalStateException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3622818116352066805L;

	public GerenciadorIllegalStateException(String mensagem) {
		super(mensagem);
	}

	public GerenciadorIllegalStateException(String mensagem, Throwable e) {
		super(mensagem, e);
	}

	public GerenciadorIllegalStateException(Throwable throwable) {
		super(throwable);
	}

}

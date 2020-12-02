package br.com.renatobergara.gerenciadordamassa.base.exception;

public class GerenciadorTestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GerenciadorTestException(String mensagem) {
		super(mensagem);
	}

	public GerenciadorTestException(String mensagem, Throwable e) {
		super(mensagem, e);
	}

	public GerenciadorTestException(Throwable throwable) {
		super(throwable);
	}

}

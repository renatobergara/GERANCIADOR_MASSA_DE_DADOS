package br.com.renatobergara.gerenciadordamassa.base.exception.xlsbeans;

import net.java.amateras.xlsbeans.XLSBeansException;

public class GerenciadorXlsBeansException extends XLSBeansException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7873245763321954366L;

	public GerenciadorXlsBeansException(String mensagem) {
		super(mensagem);
	}

	public GerenciadorXlsBeansException(String mensagem, Throwable e) {
		super(mensagem, e);
	}

	public GerenciadorXlsBeansException(Throwable throwable) {
		super(throwable);
	}

}

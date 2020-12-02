package br.com.renatobergara.gerenciadordamassa.exemplo.complanilha.relacionamento;

import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBIdentifier;
import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBIgnoreConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.java.amateras.xlsbeans.annotation.Sheet;

@Data
@XBIgnoreConverter
@EqualsAndHashCode(callSuper = false)
@Sheet(name = "RepositorioTeste")
public class Valores {

	@XBIdentifier
	private String primaryKey;

	private String valor1;

	private String valor2;

	private String resultado;

}

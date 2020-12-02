package br.com.renatobergara.gerenciadordamassa.exemplo.complanilha.comlista;

import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBIdParent;
import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBIgnoreConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@XBIgnoreConverter
@EqualsAndHashCode(callSuper = false)
public class Valores {

	@XBIdParent(classeParent = Repositorio.class)
	private String foreignKeys;

	private String valor1;

	private String valor2;

	private String resultado;

}

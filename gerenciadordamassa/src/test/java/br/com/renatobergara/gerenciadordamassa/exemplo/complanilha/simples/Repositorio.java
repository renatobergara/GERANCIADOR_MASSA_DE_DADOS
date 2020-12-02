package br.com.renatobergara.gerenciadordamassa.exemplo.complanilha.simples;

import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBIdentifier;
import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBNullToEmpty;
import lombok.Data;
import net.java.amateras.xlsbeans.annotation.Sheet;

/**
 * Nome do Repositório deverá ser o mesmo na planilha XLS.
 * 
 * A Anotação @Sheet será o nome do repositório em uma aba na planilha.
 *
 * A Anotação @XBIdentifier será a Primary key do repositório.
 * 
 * Nome da class será o idenificador na planilha @See BaseComPlanilhaTest.xls.
 * 
 * @author renato.pires
 *
 */
@Data
@Sheet(name = "RepositorioTeste")
@XBNullToEmpty
public class Repositorio {

	@XBIdentifier
	public String primaryKey;

	public String valor1;

	public String valor2;

	public String resultado;

}

package br.com.renatobergara.gerenciadordamassa.exemplo.complanilha.relacionamento;

import java.util.List;

import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBIdentifier;
import br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans.XBOneToOne;
import lombok.Data;
import net.java.amateras.xlsbeans.annotation.Sheet;

/**
 * Nome do Repositório deverá ser o mesmo na planilha XLS.
 * 
 * A Anotação @Sheet será o nome do repositório em uma aba na planilha.
 *
 * A Anotação @XBIdentifier será a Primary key do repositório.
 * 
 * A Anotação @XBComponentsRecords será o indicador para montar componentes de
 * uma @See {@link List}
 * 
 * A Anotação @XBIdParent(classeParent = Repositorio.class) representa de onde
 * irá vir a chave que irá ser montada.
 * 
 * Nome da class será o idenificador na planilha @See BaseComPlanilhaTest.xls.
 * 
 * @author renato.pires
 *
 */
@Data
@Sheet(name = "RepositorioTeste")
public class Repositorio {

	@XBIdentifier
	private String primaryKey;

	@XBOneToOne
	private Valores valores;

}

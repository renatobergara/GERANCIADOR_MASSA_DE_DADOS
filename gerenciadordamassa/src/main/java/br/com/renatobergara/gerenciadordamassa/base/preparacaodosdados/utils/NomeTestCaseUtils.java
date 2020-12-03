package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;

public class NomeTestCaseUtils {
	protected static Pattern TEST_CASE_NAME_PATTERN = Pattern.compile("^(test\\w+)(\\[([^\\]]+)\\])?$");

	public static TesteCaseInfo parseNomeTestCase(String nomeDoTesteCase) {
		Validate.notNull(nomeDoTesteCase, "O nome do teste case não pode ser nulo.");

		Matcher matcher = TEST_CASE_NAME_PATTERN.matcher(nomeDoTesteCase);

		if (matcher.matches()) {
			String nomeDoMetodo = matcher.group(1);

			String idDaLinha = matcher.group(3);

			return new TesteCaseInfo(nomeDoMetodo, idDaLinha);
		}

		throw new IllegalArgumentException("Não foi possível analisar o nome do caso de teste: " + nomeDoTesteCase);
	}

	public static String MontarNome(String nomeDometodo, String idDaLinha) {
		Validate.notEmpty(nomeDometodo, "O argumento nomeDometodo não pode ser vazio.");
		Validate.notEmpty(idDaLinha, "O argumento idDaLinha não pode ser vazio..");

		return nomeDometodo + "[" + idDaLinha + "]";
	}
}

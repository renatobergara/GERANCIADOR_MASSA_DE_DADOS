package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.utils;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
	public static boolean containsNotBlank(String[] strings) {
		if ((strings == null) || (strings.length <= 0)) {
			return false;
		}

		for (int i = 0; i < strings.length; i++) {
			if (isNotBlank(strings[i])) {
				return true;
			}
		}
		return false;
	}
}
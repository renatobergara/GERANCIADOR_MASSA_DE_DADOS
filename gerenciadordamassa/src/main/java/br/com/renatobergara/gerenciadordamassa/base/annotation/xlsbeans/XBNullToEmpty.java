package br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Traduz as strings que vem da planilha, mantendo o vazio do campo. Sem ela, os
 * campos vem com valor <i>Null</i>, que impacta na comparação dos dados
 * cadastrados.
 * 
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XBNullToEmpty {

}

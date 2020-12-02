package br.com.renatobergara.gerenciadordamassa.base.annotation.xlsbeans;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XBComponentsRecords {
	boolean optional() default false;

	String terminateLabel() default "";

	int headerRow() default -1;

	int headerColumn() default -1;

	net.java.amateras.xlsbeans.annotation.RecordTerminal terminal() default net.java.amateras.xlsbeans.annotation.RecordTerminal.Empty;

	int range() default 1;

	int bottom() default 1;

	int headerLimit() default 0;
}

package br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.excel;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;

import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.CarregaElementos;
import br.com.renatobergara.gerenciadordamassa.base.preparacaodosdados.carregaelementos.Elementos;
import jxl.Workbook;
import jxl.WorkbookSettings;

public class CarregaElementosExcel implements CarregaElementos {
	private static final Log logger = LogFactory.getLog(CarregaElementosExcel.class);

	// Padrão JXL
	protected String encoding = "ISO-8859-1";

	@Override
	public Elementos carregaElementos(Class<?> clazz) {
		Validate.notNull(clazz, "O argumento Clazz não pode ser vazio.");

		String shortName = ClassUtils.getShortClassName(clazz);
		String fileName = shortName + ".xls";

		ClassPathResource resource = new ClassPathResource(fileName, clazz);

		if (!resource.exists()) {
			logger.debug("CarregaElementos(clazz = " + clazz + ") - Arquivo excel não encontrado: " + fileName);
		}

		InputStream stream;
		try {
			stream = resource.getInputStream();
		} catch (IOException e) {
			logger.debug("IOException encontrando arquivo Excel.", e);

			return null;
		}
		try {
			WorkbookSettings settings = new WorkbookSettings();
			settings.setEncoding(this.encoding);
			settings.setGCDisabled(true);

			Workbook workbook = Workbook.getWorkbook(stream, settings);

			if (workbook == null) {
				logger.warn("CarregaElementos(clazz = " + clazz + ") - Erro ao carregar arquivo Excel: " + fileName);
			}

			if (logger.isInfoEnabled()) {
				logger.info("CarregaElementos(clazz = " + clazz + ") - Arquivo Excel carregado.");
			}

			return new ElementosExcelAdapter(workbook);
		} catch (Exception e) {
			logger.warn("CarregaElementos(clazz = " + clazz + ") - Erro ao carregar arquivo Excel.");

			throw new DadosExcelException("Exception abrindo arquivo excel para a class " + clazz.getName(), e);
		}
	}

	public String getEncoding() {
		return this.encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
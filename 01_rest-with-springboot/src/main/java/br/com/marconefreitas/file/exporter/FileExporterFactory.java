package br.com.marconefreitas.file.exporter;

import br.com.marconefreitas.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FileExporterFactory {

    private static final Logger log = LoggerFactory.getLogger(FileExporterFactory.class);

    @Autowired
    private ApplicationContext applicationContext;

    public FileExporter getExporter(String acceptHeader) throws Exception {
        if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_XLSX_VALUE)){
            return applicationContext.getBean(XlsxExporter.class);
        } else if (acceptHeader.equalsIgnoreCase("text/csv")){
            return applicationContext.getBean(CsvExporter.class);
        } else {
            throw new BadRequestException();
        }
    }
}

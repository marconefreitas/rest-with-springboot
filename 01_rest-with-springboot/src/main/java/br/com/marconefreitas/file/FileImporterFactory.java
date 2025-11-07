package br.com.marconefreitas.file;

import br.com.marconefreitas.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FileImporterFactory {

    private Logger log = LoggerFactory.getLogger(FileImporterFactory.class);

    @Autowired
    private ApplicationContext applicationContext;

    public FileImporter getImporter(String fileName) throws Exception {
        if (fileName.endsWith(".xlsx")){
            return applicationContext.getBean(XlsxImporter.class);
        } else if (fileName.endsWith(".csv")){
            return applicationContext.getBean(CsvImporter.class);
        } else {
            throw new BadRequestException();
        }
    }
}

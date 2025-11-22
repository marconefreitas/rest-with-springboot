package br.com.marconefreitas.file.exporter;

import br.com.marconefreitas.data.dto.PersonDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvExporter implements FileExporter{

    @Override
    public Resource exportFile(List<PersonDTO> people) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        CSVFormat  csvFormat = CSVFormat.Builder.create()
                .setHeader("ID", "First Name", "Last name", "Address", "Gender", "Enabled")
                .setSkipHeaderRecord(false)
                .build();
        try( CSVPrinter printer = new CSVPrinter(writer, csvFormat) ){
            for(PersonDTO person : people){
                printer.printRecord(
                        person.getId(),
                        person.getFirstName(),
                        person.getLastName(),
                        person.getAddress(),
                        person.getGender(),
                        person.getEnabled());
            }
        }

        return new ByteArrayResource(outputStream.toByteArray());
    }
}

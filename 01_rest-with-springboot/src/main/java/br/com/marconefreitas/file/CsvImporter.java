package br.com.marconefreitas.file;

import br.com.marconefreitas.data.dto.PersonDTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvImporter implements FileImporter {
    @Override
    public List<PersonDTO> importFile(InputStream inputStream) throws Exception {
        CSVFormat format = CSVFormat.Builder.create()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build();
        Iterable<CSVRecord> records =  format.parse(new InputStreamReader(inputStream));
        return parserRecordsDto(records);
    }

    private List<PersonDTO> parserRecordsDto(Iterable<CSVRecord> records) {
        List<PersonDTO> people = new ArrayList<PersonDTO>();
        for(CSVRecord record : records) {
            PersonDTO personDTO = new PersonDTO();
            personDTO.setFirstName(record.get("first_name"));
            personDTO.setLastName(record.get("last_name"));
            personDTO.setAddress(record.get("address"));
            personDTO.setGender(record.get("gender"));
            personDTO.setEnabled(true);
            people.add(personDTO);
        }
        return people;
    }
}

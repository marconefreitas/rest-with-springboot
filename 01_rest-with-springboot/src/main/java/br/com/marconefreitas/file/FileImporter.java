package br.com.marconefreitas.file;

import br.com.marconefreitas.data.dto.PersonDTO;

import java.io.InputStream;
import java.util.List;

public interface FileImporter {
    List<PersonDTO> importFile(InputStream inputStream) throws Exception;
}

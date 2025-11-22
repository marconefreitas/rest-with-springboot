package br.com.marconefreitas.file.exporter;

import br.com.marconefreitas.data.dto.PersonDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class XlsxExporter implements FileExporter {


    @Override
    public Resource exportFile(List<PersonDTO> people) throws Exception {
        try(Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("People");
            Row header = sheet.createRow(0);
            String[] headers = {"ID", "First Name", "Last name", "Address", "Gender", "Enabled"};

            for(int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderCellStyle(workbook));
            }
            int rowIndex = 1;
            for(PersonDTO personDTO : people) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(personDTO.getId());
                row.createCell(1).setCellValue(personDTO.getFirstName());
                row.createCell(2).setCellValue(personDTO.getLastName());
                row.createCell(3).setCellValue(personDTO.getAddress());
                row.createCell(4).setCellValue(personDTO.getGender());
                row.createCell(5).setCellValue(
                        personDTO.getEnabled() != null && personDTO.getEnabled() ? "Sim": "Nao");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        }

    }

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}

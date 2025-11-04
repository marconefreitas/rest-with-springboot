package br.com.marconefreitas.controllers;

import br.com.marconefreitas.controllers.docs.FileControllerDocs;
import br.com.marconefreitas.data.dto.UploadFileResponseDTO;
import br.com.marconefreitas.services.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/file")
public class FileController implements FileControllerDocs {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    @PostMapping("/uploadFile")
    public UploadFileResponseDTO uploadFile(@RequestParam("file") MultipartFile file) {
        var result = fileStorageService.storeFile(file);
        var fileDownloadURI = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/v1/file/downloadFile/")
                .path(result)
                .toUriString();
        return new UploadFileResponseDTO(result, fileDownloadURI, file.getContentType(), file.getSize());
    }

    @Override
    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponseDTO> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files)  {
        return Arrays.stream(files)
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileName, HttpServletRequest req) throws IOException {
        Resource res = fileStorageService.loadFileAsResource(fileName);
        String contentType = null;
        try{
            contentType = req.getServletContext().getMimeType(res.getFile().getAbsolutePath());
        }catch (Exception ex){
            logger.error("Could not determine file type.", ex);
        }
        if(contentType == null){
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + res.getFilename() + "\"")
                .body(res);
    }
}

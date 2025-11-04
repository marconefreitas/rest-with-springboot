package br.com.marconefreitas.services;

import br.com.marconefreitas.config.FileStorageConfig;
import br.com.marconefreitas.controllers.FileController;
import br.com.marconefreitas.exception.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);


    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageConfig fileStorageConfig) {
        this.fileStorageLocation = Paths.get(fileStorageConfig
                        .getUploadDir())
                .toAbsolutePath()
                .normalize();
        try{
            logger.info("Creating directories");
            Files.createDirectories(fileStorageLocation);
        } catch(Exception e){
            logger.error("Could not create directory: {}", fileStorageLocation );
            throw new FileStorageException("Could not create directory: " + fileStorageLocation, e);
        }
    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try{
            if(fileName.contains("..")){
                logger.error("Filename contains invalid path sequence: {}", fileName);
                throw new FileStorageException("Filename contains invalid path sequence: " + fileName);
            }
            logger.info("Saving file");
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        }catch (Exception e){
            logger.error("Could not store file: {}", fileName );
            throw new FileStorageException("Could not store file: " + fileName, e);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
              Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
              Resource res = new UrlResource(filePath.toUri());
              if(res.exists()){
                  return res;
              }else {
                  throw new FileStorageException("File not found:  " + fileName);
              }
        } catch(Exception e){
            logger.error("File not found: {}", fileName );
            throw new FileStorageException("File not found: " + fileName, e);
        }
    }
}

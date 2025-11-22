package br.com.marconefreitas.services;

import br.com.marconefreitas.config.EmailConfig;
import br.com.marconefreitas.data.dto.request.EmailRequestDTO;
import br.com.marconefreitas.mail.EmailSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class EmailService {

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private EmailConfig emailConfig;

    public void sendSimpleMail(EmailRequestDTO emailRequestDTO) {
        emailSender.to(emailRequestDTO.getTo())
                .withSubject(emailRequestDTO.getSubject())
                .withMessage(emailRequestDTO.getBody())
                .send(emailConfig);
    }

    public void sendEMailWithAttachment(String emailRequestJson, MultipartFile file) {
        File tempFile = null;
        try {
            EmailRequestDTO emailRequestDTO = new ObjectMapper()
                    .readValue(emailRequestJson, EmailRequestDTO.class);
            tempFile = File.createTempFile("attachment", file.getOriginalFilename());
            file.transferTo(tempFile);

            emailSender.to(emailRequestDTO.getTo())
                    .withSubject(emailRequestDTO.getSubject())
                    .withMessage(emailRequestDTO.getBody())
                    .attach(tempFile.getAbsolutePath())
                    .send(emailConfig);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao tentar enviar email com anexo", e);
        } finally {
            if(tempFile != null && tempFile.exists()){
                if(tempFile.delete()) System.out.println("Arquivo temporario deletado");
            }
        }
    }
}

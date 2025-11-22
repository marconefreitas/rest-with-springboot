package br.com.marconefreitas.controllers;

import br.com.marconefreitas.controllers.docs.EmailControllerDocs;
import br.com.marconefreitas.data.dto.request.EmailRequestDTO;
import br.com.marconefreitas.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/email")
public class EmailController implements EmailControllerDocs {

    @Autowired
    private EmailService emailService;



    @Override
    @PostMapping
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequestDTO) {
        emailService.sendSimpleMail(emailRequestDTO);
        return new ResponseEntity<>("Email enviado", HttpStatus.OK);
    }

    @Override
    @PostMapping("/withAttachment")
    public ResponseEntity<String> sendEmailWithAttachment(
            @RequestParam("emailRequest") String emailRequest,
            @RequestParam("att") MultipartFile file) {
        emailService.sendEMailWithAttachment(emailRequest, file);
        return new ResponseEntity<>("Email enviado", HttpStatus.OK);
    }
}

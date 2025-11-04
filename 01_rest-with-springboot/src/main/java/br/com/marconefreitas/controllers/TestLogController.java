package br.com.marconefreitas.controllers;

import br.com.marconefreitas.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestLogController {

    private Logger logger = LoggerFactory.getLogger(TestLogController.class.getName());


    @GetMapping("/test")
    public String testLog(){
        logger.info("Log info");
        logger.warn("Log warn");
        logger.debug("Log debug");
        logger.error("Log ERROR");
        return "Logs gerenated succesfully";
    }
}

package com.departure.helper.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.departure.helper.exception.FileFormatException;
import com.departure.helper.exception.FileProcessingException;
import com.departure.helper.service.FileUploadService;
import com.departure.helper.util.XlsxFileValidator;

@Controller
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping("/")
    public String showUploadForm(Model model) {

        return "index";
    }

    @PostMapping("/")
    public ResponseEntity<InputStreamResource> processExcelFile(@RequestParam("file") MultipartFile file, Model model) {

        XlsxFileValidator.validate(file);

        InputStreamResource resource = fileUploadService.processExcelFile(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=generated-files.zip")
                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @ExceptionHandler({FileFormatException.class, FileProcessingException.class})
    public String handleFileFormatException(RuntimeException ex, Model model) {

        model.addAttribute("errorMessage", ex.getMessage());
        return "index";
    }
}

package com.departure.helper.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.departure.helper.service.DepartureNotificationTemplateService;

@RestController
public class DepartureNotificationTemplateController {

    @Autowired
    DepartureNotificationTemplateService templateService;

    @GetMapping("/template")
    public ResponseEntity<InputStreamResource> downloadTemplate() {

        ByteArrayOutputStream fileOut = new ByteArrayOutputStream();

        Workbook workbook = templateService.getDefaultDepartureNotificationTemplate();
        try {
            workbook.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        InputStreamResource inputStreamResource = new InputStreamResource(
                new ByteArrayInputStream(fileOut.toByteArray()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=Departure notification template.xls")
                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                .body(inputStreamResource);
    }
}

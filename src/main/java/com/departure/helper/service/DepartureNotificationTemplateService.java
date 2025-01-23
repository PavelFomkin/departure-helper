package com.departure.helper.service;

import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.departure.helper.exception.ExcelDataProcessingException;

@Service
public class DepartureNotificationTemplateService {

    @Autowired
    private ResourceLoader resourceLoader;

    public HSSFWorkbook getDefaultDepartureNotificationTemplate() {

        Resource resource = resourceLoader.getResource("classpath:/static/excel/template.xls");
        try (InputStream inputStream = resource.getInputStream()) {
            return new HSSFWorkbook(inputStream);
        } catch (Exception e) {
            throw new ExcelDataProcessingException("Template file is not found in resources.");
        }
    }
}

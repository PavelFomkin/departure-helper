package com.departure.helper.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import com.departure.helper.exception.ExcelDataProcessingException;

@Service
public class ExcelArchiveGenerator {

    public InputStreamResource createArchive(Map<String, Workbook> fileNamesToWorkbooks) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
            for (Entry<String, Workbook> fileNameToWorkbook : fileNamesToWorkbooks.entrySet()) {
                ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
                fileNameToWorkbook.getValue().write(fileOut);
                fileOut.close();

                byte[] fileBytes = fileOut.toByteArray();
                ZipEntry zipEntry = new ZipEntry(fileNameToWorkbook.getKey() + ".xls");
                zipOut.putNextEntry(zipEntry);
                zipOut.write(fileBytes);
                zipOut.closeEntry();
            }
        } catch (IOException e) {
            throw new ExcelDataProcessingException("Unable to create zip archive.");
        }

        return new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
    }

}

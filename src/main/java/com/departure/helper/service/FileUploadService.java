package com.departure.helper.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.departure.helper.exception.FileProcessingException;
import com.departure.helper.model.DepartureNotificationData;

@Service
public class FileUploadService {

    @Autowired
    private DepartureNotificationDataExcelConverter converter;

    @Autowired
    private DepartureNotificationGenerator generator;

    public InputStreamResource processExcelFile(MultipartFile file) {

        List<DepartureNotificationData> departureNotificationData;
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            departureNotificationData = converter.convert(workbook);
        } catch (IllegalStateException | IOException e) {
            throw new FileProcessingException("Not valid data in the file, please review the format of the data.");
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
            for (DepartureNotificationData singlePersonData : departureNotificationData) {
                Workbook departureNotification;
                try {
                    departureNotification = generator.generate(singlePersonData);
                } catch (RuntimeException e) {
                    throw new FileProcessingException(String.format(
                            "Unable to generate departure notification for %s",
                            singlePersonData.getFullName()));
                }

                ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
                departureNotification.write(fileOut);
                fileOut.close();

                byte[] fileBytes = fileOut.toByteArray();
                ZipEntry zipEntry = new ZipEntry(singlePersonData.getFullName() + ".xls");
                zipOut.putNextEntry(zipEntry);
                zipOut.write(fileBytes);
                zipOut.closeEntry();
            }
        } catch (IOException e) {
            throw new FileProcessingException("Unable to create zip archive.");
        }

        return new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
    }
}

package com.departure.helper.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.departure.helper.exception.ExcelDataProcessingException;
import com.departure.helper.model.DepartureNotificationData;

@Service
public class DepartureNotificationService {

    @Autowired
    private DepartureNotificationDataExcelParser excelParser;

    @Autowired
    private DepartureNotificationGenerator departureNotificationGenerator;

    @Autowired
    private ExcelArchiveGenerator archiveGenerator;

    public InputStreamResource processExcelFile(MultipartFile file, MultipartFile customTemplate) {

        List<DepartureNotificationData> uploadedFileData = parseUploadedFile(file);

        Map<String, Workbook> fileNameToWorkbookMap = prepareDepartureNotificationFiles(uploadedFileData, customTemplate);

        return archiveGenerator.createArchive(fileNameToWorkbookMap);
    }

    private List<DepartureNotificationData> parseUploadedFile(MultipartFile file) {

        try (InputStream inputStream = file.getInputStream()) {
            return excelParser.parse(new XSSFWorkbook(inputStream));
        } catch (IllegalStateException | IOException e) {
            throw new ExcelDataProcessingException("Not valid data in the file, please review the format of the data.");
        }
    }

    private Map<String, Workbook> prepareDepartureNotificationFiles(List<DepartureNotificationData> parsedData,
            MultipartFile customTemplate) {

        return parsedData.stream()
                .collect(Collectors.toMap(DepartureNotificationData::getFullName, (data) -> {
                    try {
                        return departureNotificationGenerator.generate(data, customTemplate);
                    } catch (IOException | RuntimeException e) {
                        throw new ExcelDataProcessingException(String.format(
                                "Unable to generate departure notification for %s, please review selected template or user data",
                                data.getFullName()));
                    }
                }));
    }
}

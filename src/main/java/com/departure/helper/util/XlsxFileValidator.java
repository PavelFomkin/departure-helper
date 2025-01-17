package com.departure.helper.util;

import java.io.IOException;

import org.apache.logging.log4j.util.Strings;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.departure.helper.exception.FileFormatException;

public class XlsxFileValidator {

    private static final String XLSX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String XLSX_TYPE = ".xlsx";

    public static void validate(MultipartFile file) {

        String filename = file.getOriginalFilename();
        if (Strings.isEmpty(filename) || !filename.endsWith(XLSX_TYPE)) {
            throw new FileFormatException("Invalid file type. Please upload an .xlsx file.");
        }

        String mimeType = file.getContentType();
        if (Strings.isEmpty(mimeType) || !XLSX_MIME_TYPE.equals(mimeType)) {
            throw new FileFormatException("Invalid file type. Please upload a valid .xlsx file.");
        }

        try (var inputStream = file.getInputStream()) {
            new XSSFWorkbook(inputStream);  // Try to load as XSSFWorkbook
        } catch (IOException e) {
            throw new FileFormatException("File is not a valid .xlsx Excel file.");
        }
    }
}

package com.departure.helper.util;

import org.apache.logging.log4j.util.Strings;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.departure.helper.exception.FileFormatException;

public class ExcelFileValidator {

    private static final String XLSX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String XLS_MIME_TYPE = "application/vnd.ms-excel";
    private static final String XLSX_TYPE = ".xlsx";
    private static final String XLS_TYPE = ".xls";

    public static void validateXlsx(MultipartFile file) {

        String filename = file.getOriginalFilename();
        if (Strings.isEmpty(filename) || !filename.endsWith(XLSX_TYPE)) {
            throw new FileFormatException("Invalid file type. Please upload an .xlsx file.");
        }

        String mimeType = file.getContentType();
        if (Strings.isEmpty(mimeType) || !XLSX_MIME_TYPE.equals(mimeType)) {
            throw new FileFormatException("Invalid file type. Please upload a valid .xlsx file.");
        }

        // TODO:investigate why throws OutOfMemoryError
//        try (var inputStream = file.getInputStream()) {
//            new XSSFWorkbook(inputStream);  // Try to load as XSSFWorkbook
//        } catch (Exception e) {
//            throw new FileFormatException("File is not a valid .xlsx Excel file.");
//        }
    }

    public static void validateXls(MultipartFile file) {

        String filename = file.getOriginalFilename();
        if (Strings.isEmpty(filename) || !filename.endsWith(XLS_TYPE)) {
            throw new FileFormatException("Custom template has invalid file type. Please upload an .xls file.");
        }

        String mimeType = file.getContentType();
        if (Strings.isEmpty(mimeType) || !XLS_MIME_TYPE.equals(mimeType)) {
            throw new FileFormatException("Custom template has invalid file type. Please upload a valid .xls file.");
        }

        try (var inputStream = file.getInputStream()) {
            new HSSFWorkbook(inputStream);  // Try to load as XSSFWorkbook
        } catch (Exception e) {
            throw new FileFormatException("Custom template file is not a valid .xls Excel file.");
        }
    }
}

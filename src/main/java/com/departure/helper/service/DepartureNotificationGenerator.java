package com.departure.helper.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import com.departure.helper.exception.FileProcessingException;
import com.departure.helper.model.DepartureNotificationData;
import com.departure.helper.model.DepartureNotificationDataPositions;

@Service
public class DepartureNotificationGenerator {

    private static final String DEPARTURE_NOTIFICATION_TEMPLATE_PATH = "src/main/resources/static/excel/template.xls";
    private static final SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");

    public Workbook generate(DepartureNotificationData departureNotificationData) {

        Workbook workbook = readDepartureNotificationTemplate();
        Sheet sheet = workbook.getSheetAt(0);

        String fullName = departureNotificationData.getFullName();
        String[] fullNameParts = fullName.split(" ", 2);
        String lastName = fullNameParts[0];
        String firstAndMiddleName = fullNameParts[1];
        fillDepartureNotificationData(sheet, DepartureNotificationDataPositions.LAST_NAME_FIRST_PLACE, lastName);
        fillDepartureNotificationData(sheet, DepartureNotificationDataPositions.LAST_NAME_SECOND_PLACE, lastName);
        fillDepartureNotificationData(sheet, DepartureNotificationDataPositions.FIRST_AND_MIDDLE_NAME_FIRST_PLACE, firstAndMiddleName);
        fillDepartureNotificationData(sheet, DepartureNotificationDataPositions.FIRST_AND_MIDDLE_NAME_SECOND_PLACE, firstAndMiddleName);

        Date birthDate = departureNotificationData.getBirthDate();
        fillDepartureNotificationData(sheet, DepartureNotificationDataPositions.BIRTH_DATE_FIRST_PLACE, birthDate);
        fillDepartureNotificationData(sheet, DepartureNotificationDataPositions.BIRTH_DATE_SECOND_PLACE, birthDate);

        Date departureDate = departureNotificationData.getDepartureDate();
        fillDepartureNotificationData(sheet, DepartureNotificationDataPositions.DEPARTURE_DATE, departureDate);

        return workbook;
    }

    private HSSFWorkbook readDepartureNotificationTemplate() {

        try (InputStream inputStream = Files.newInputStream(Paths.get(DEPARTURE_NOTIFICATION_TEMPLATE_PATH))) {
            return new HSSFWorkbook(inputStream);
        } catch (Exception e) {
            throw new FileProcessingException("Template file not found in resources.");
        }
    }

    private void fillDepartureNotificationData(Sheet sheet, DepartureNotificationDataPositions positions, String value) {

        Row row = sheet.getRow(positions.getRowIndex());
        List<Integer> cellIndexes = positions.getCellIndexes();
        String[] lettersToFill = value.split("");

        for (int i = 0; i < lettersToFill.length && i < cellIndexes.size(); i++) {
            row.getCell(cellIndexes.get(i)).setCellValue(lettersToFill[i]);
        }
    }

    private void fillDepartureNotificationData(Sheet sheet, DepartureNotificationDataPositions positions, Date value) {

        Row row = sheet.getRow(positions.getRowIndex());
        List<Integer> cellIndexes = positions.getCellIndexes();
        String[] lettersToFill = convertToString(value).split("");

        for (int i = 0; i < lettersToFill.length && i < cellIndexes.size(); i++) {
            row.getCell(cellIndexes.get(i)).setCellValue(Integer.parseInt(lettersToFill[i]));
        }
    }

    private String convertToString(Date date) {

        return formatter.format(date);
    }
}


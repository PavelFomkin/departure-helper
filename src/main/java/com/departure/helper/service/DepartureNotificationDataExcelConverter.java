package com.departure.helper.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import com.departure.helper.exception.FileProcessingException;
import com.departure.helper.model.ColumnProcessor;
import com.departure.helper.model.DepartureNotificationData;
import com.departure.helper.model.DepartureNotificationData.Builder;

@Service
public class DepartureNotificationDataExcelConverter {

    private static final Map<String, ColumnProcessor> columnProcessors = Map.of(
            "фио", (builder, cell) -> builder.fullName(cell.getStringCellValue()),
            "дата рождения", (builder, cell) -> builder.birthDate(cell.getDateCellValue()),
            "дата снятия", (builder, cell) -> builder.departureDate(cell.getDateCellValue()));

    public List<DepartureNotificationData> convert(Workbook workbook) {

        List<DepartureNotificationData> convertedData = new ArrayList<>();

        Sheet workingSheet = workbook.getSheetAt(0);
        Map<Integer, ColumnProcessor> columnIndexToProcessor = getAvailableProcessors(workingSheet.getRow(0));
        if (columnIndexToProcessor.size() < columnProcessors.size()) {
            throw new FileProcessingException(String.format(
                    "Not found all required columns in the uploaded document, the list of required columns (case insensitive): %s",
                    columnProcessors.keySet()));
        }

        for (int i = 1; i <= workingSheet.getLastRowNum(); i++) {
            var departureNotificationData = convertRow(workingSheet.getRow(i), columnIndexToProcessor);
            convertedData.add(departureNotificationData);
        }

        return convertedData;
    }

    private Map<Integer, ColumnProcessor> getAvailableProcessors(Row headerRow) {

        Map<Integer, ColumnProcessor> columnIndexToProcessor = new HashMap<>();

        Set<String> parsableColumnNames = columnProcessors.keySet();
        headerRow.cellIterator().forEachRemaining(cell -> {
            String lowerCaseColumnName = cell.getStringCellValue().toLowerCase();
            if (parsableColumnNames.contains(lowerCaseColumnName)) {
                columnIndexToProcessor.put(cell.getColumnIndex(), columnProcessors.get(lowerCaseColumnName));
            }
        });

        return columnIndexToProcessor;
    }

    private DepartureNotificationData convertRow(
            Row row, Map<Integer, ColumnProcessor> columnIndexToProcessor) {

        Builder builder = DepartureNotificationData.builder();

        columnIndexToProcessor.forEach((index, processor) ->
                processor.process(builder, row.getCell(index)));

        DepartureNotificationData data = builder.build();
        if (StringUtils.isEmpty(data.getFullName()) || data.getBirthDate() == null || data.getDepartureDate() == null) {
            throw new FileProcessingException(String.format("The data is missed in row %d. The data is mandatory for columns (case insensitive): %s",
                    row.getRowNum(), columnProcessors.keySet()));
        }

        return data;
    }
}

package com.departure.helper.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import com.departure.helper.exception.ExcelDataProcessingException;
import com.departure.helper.model.ColumnProcessor;
import com.departure.helper.model.DepartureNotificationData;
import com.departure.helper.model.DepartureNotificationData.Builder;

@Service
public class DepartureNotificationDataExcelParser {

    private static final Map<String, ColumnProcessor> columnProcessors = Map.of(
            "фио", (builder, cell) -> builder.fullName(cell.getStringCellValue().toUpperCase()),
            "дата рождения", (builder, cell) -> builder.birthDate(cell.getDateCellValue()),
            "дата снятия", (builder, cell) -> builder.departureDate(cell.getDateCellValue()));

    public List<DepartureNotificationData> parse(Workbook workbook) {

        Sheet workingSheet = workbook.getSheetAt(0);
        Map<Integer, ColumnProcessor> columnIndexToProcessor = getAvailableProcessors(workingSheet.getRow(0));
        if (columnIndexToProcessor.size() < columnProcessors.size()) {
            throw new ExcelDataProcessingException(String.format(
                    "Not found all required columns in the uploaded document, the list of required columns (case insensitive): %s",
                    columnProcessors.keySet()));
        }

        return IntStream.range(1, workingSheet.getLastRowNum() + 1)
                .mapToObj(workingSheet::getRow)
                .map(row -> parseRow(row, columnIndexToProcessor))
                .filter(Objects::nonNull)  // remove unprocessable rows (cell type is wrong or empty fullName)
                .collect(Collectors.toList());
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

    private DepartureNotificationData parseRow(Row row, Map<Integer, ColumnProcessor> columnIndexToProcessor) {

        Builder builder = DepartureNotificationData.builder();

        try {
            columnIndexToProcessor.forEach((index, processor) ->
                    processor.process(builder, row.getCell(index)));
        } catch (RuntimeException e) {
            return null;
        }

        DepartureNotificationData data = builder.build();
        if (Strings.isBlank(data.getFullName())) {
            return null;
        } else if (data.getBirthDate() == null || data.getDepartureDate() == null) {
            throw new ExcelDataProcessingException(String.format(
                    "The data is missed in row %d. The data is mandatory for columns (case insensitive): %s",
                    row.getRowNum(), columnProcessors.keySet()));
        }

        return data;
    }
}

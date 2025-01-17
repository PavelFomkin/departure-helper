package com.departure.helper.model;

import java.util.List;

/**
 * Positions of cells to fill them correctly
 * <p>
 * To calculate it, you can use
 * https://excel-col-converter.vercel.app/
 * For example if we want to convert AQ10 cell:
 * AQ = 43 (in the convertor by the link)
 * To set the position correctly we should decrease this numbers to 1.
 * So, the position for AQ10: row 9, cell 42.
 */
public enum DepartureNotificationDataPositions {
    LAST_NAME_FIRST_PLACE(9, List.of(42, 46, 50, 54, 58, 62, 66, 70, 74, 78, 82, 86, 90, 94, 98, 102, 106, 110, 114, 118, 122, 126, 130, 134, 138, 142, 146, 150, 154, 158)),
    LAST_NAME_SECOND_PLACE(56, List.of(42, 46, 50, 54, 58, 62, 66, 70, 74, 78, 82, 86, 90, 94, 98, 102, 106, 110, 114, 118, 122, 126, 130, 134, 138, 142, 146, 150, 154, 158)),
    FIRST_AND_MIDDLE_NAME_FIRST_PLACE(11, List.of(42, 46, 50, 54, 58, 62, 66, 70, 74, 78, 82, 86, 90, 94, 98, 102, 106, 110, 114, 118, 122, 126, 130, 134, 138, 142, 146, 150, 154, 158)),
    FIRST_AND_MIDDLE_NAME_SECOND_PLACE(58, List.of(42, 46, 50, 54, 58, 62, 66, 70, 74, 78, 82, 86, 90, 94, 98, 102, 106, 110, 114, 118, 122, 126, 130, 134, 138, 142, 146, 150, 154, 158)),
    BIRTH_DATE_FIRST_PLACE(14, List.of(38, 42, 54, 58, 66, 70, 74, 78)),
    BIRTH_DATE_SECOND_PLACE(61, List.of(54, 58, 70, 74, 82, 86, 90, 94)),
    DEPARTURE_DATE(14, List.of(118, 122, 134, 138, 146, 150, 154, 158));

    private final Integer rowIndex;
    private final List<Integer> cellIndexes;

    DepartureNotificationDataPositions(Integer rowIndex, List<Integer> cellIndexes) {

        this.rowIndex = rowIndex;
        this.cellIndexes = cellIndexes;
    }

    public Integer getRowIndex() {

        return rowIndex;
    }

    public List<Integer> getCellIndexes() {

        return cellIndexes;
    }
}

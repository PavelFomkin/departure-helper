package com.departure.helper.model;

import org.apache.poi.ss.usermodel.Cell;

import com.departure.helper.model.DepartureNotificationData.Builder;

@FunctionalInterface
public interface ColumnProcessor {

    void process(Builder builder, Cell cell);
}

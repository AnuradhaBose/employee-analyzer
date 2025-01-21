package com.bigcompany;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvFileReader implements EmployeeFileReader {
    @Override
    public List<String[]> readFile(BufferedReader reader) throws IOException {
        List<String[]> csvData = new ArrayList<>();
        String line;
        boolean isFirstRow = true;

        while ((line = reader.readLine()) != null) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            String[] columns = line.split(",");

            if (columns.length >= 4) {
                if (columns.length == 4) {
                    columns = new String[]{columns[0], columns[1], columns[2], columns[3], null};
                } else if (columns.length == 5 && columns[4].isEmpty()) {
                    columns[4] = null;
                }
                csvData.add(columns);
            } else {
                System.err.println("Skipping invalid row: " + line);
            }
        }
        return csvData;
    }
}


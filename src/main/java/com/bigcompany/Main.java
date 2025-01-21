package com.bigcompany;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String csvPath = "employeeList.csv";

        EmployeeFactory employeeFactory = new EmployeeFactory();
        EmployeeFileReader fileReader = new CsvFileReader();
        List<ValidationStrategy> strategies = List.of(new SalaryValidationStrategy());

        EmployeeAnalyzer analyzer = new EmployeeAnalyzer(employeeFactory, strategies);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Main.class.getClassLoader().getResourceAsStream(csvPath)))) {

            List<String[]> csvData = fileReader.readFile(reader);
            analyzer.analyze(csvData);
        } catch (NullPointerException e) {
            System.err.println("File not found in resources: " + csvPath);
        }catch(Exception e){
            System.err.println("Something went wrong: " + e.getMessage());
        }
    }
}

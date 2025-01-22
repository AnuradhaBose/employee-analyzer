package com.bigcompany.main;

import com.bigcompany.analyzer.EmployeeAnalyzer;
import com.bigcompany.factory.EmployeeFactory;
import com.bigcompany.reader.CsvFileReader;
import com.bigcompany.reader.EmployeeFileReader;
import com.bigcompany.strategy.ReportingValidationStrategy;
import com.bigcompany.strategy.SalaryValidationStrategy;
import com.bigcompany.strategy.ValidationStrategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException {
        String csvPath = "employeeList.csv";

        EmployeeFactory employeeFactory = new EmployeeFactory();
        EmployeeFileReader fileReader = new CsvFileReader();
        List<ValidationStrategy> strategies = List.of(new SalaryValidationStrategy(),new ReportingValidationStrategy());

        EmployeeAnalyzer analyzer = new EmployeeAnalyzer(employeeFactory, strategies);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Main.class.getClassLoader().getResourceAsStream(csvPath)))) {

            List<String[]> csvData = fileReader.readFile(reader);

            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.submit(() -> analyzer.analyze(csvData));

            executor.shutdown();

        } catch (NullPointerException e) {
            System.err.println("File not found in resources: " + csvPath);
        }catch(Exception e){
            System.err.println("Something went wrong: " + e.getMessage());
        }
    }
}

package com.bigcompany.strategy;

import com.bigcompany.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SalaryValidationStrategyTest {

    private SalaryValidationStrategy salaryValidationStrategy;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        salaryValidationStrategy = new SalaryValidationStrategy();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void validate_whenManagerSalaryIsBelowMinimum_shouldPrintWarning() {

        Employee manager = new Employee.Builder()
                .setId(1)
                .setFirstName("John")
                .setLastName("Doe")
                .setSalary(80000)
                .build();
        List<Employee> subordinates = Arrays.asList(
                new Employee.Builder()
                        .setId(2)
                        .setFirstName("Alice")
                        .setLastName("Tailor")
                        .setSalary(60000)
                        .build(),
                new Employee.Builder()
                        .setId(3)
                        .setFirstName("Bill")
                        .setLastName("Doe")
                        .setSalary(70000)
                        .build()
        );
        salaryValidationStrategy.validate(manager, subordinates);
        String output = outputStreamCaptor.toString();
        assertTrue(!output.contains("Manager John (ID: 1) earns"));
        assertTrue(!output.contains("less than the minimum required"));
    }

    @Test
    void validate_whenManagerSalaryIsAboveMaximum_shouldPrintWarning() {
        Employee manager = new Employee.Builder()
                .setId(1)
                .setFirstName("John")
                .setLastName("Doe")
                .setSalary(80000)
                .build();
        List<Employee> subordinates = Arrays.asList(
                new Employee.Builder()
                        .setId(2)
                        .setFirstName("Alice")
                        .setLastName("Tailor")
                        .setSalary(60000)
                        .build(),
                new Employee.Builder()
                        .setId(3)
                        .setFirstName("Bill")
                        .setLastName("Doe")
                        .setSalary(70000)
                        .build()
        );


        salaryValidationStrategy.validate(manager, subordinates);


        String output = outputStreamCaptor.toString();
        assertTrue(!output.contains("Manager John (ID: 1) earns"));
        assertTrue(!output.contains("more than the maximum allowed"));
    }

    @Test
    void validate_whenManagerSalaryIsWithinRange_shouldNotPrintWarnings() {
        Employee manager = new Employee.Builder()
                .setId(1)
                .setFirstName("John")
                .setLastName("Doe")
                .setSalary(80000)
                .build();
        List<Employee> subordinates = Arrays.asList(
                new Employee.Builder()
                        .setId(2)
                        .setFirstName("Alice")
                        .setLastName("Tailor")
                        .setSalary(60000)
                        .build(),
                new Employee.Builder()
                        .setId(3)
                        .setFirstName("Bill")
                        .setLastName("Doe")
                        .setSalary(70000)
                        .build()
        );

        salaryValidationStrategy.validate(manager, subordinates);
        String output = outputStreamCaptor.toString();
        assertTrue(output.isEmpty(), "Output should be empty when manager salary is within range.");
    }
}

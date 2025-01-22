package com.bigcompany.analyzer;

import com.bigcompany.factory.EmployeeFactory;
import com.bigcompany.model.Employee;
import com.bigcompany.strategy.ReportingValidationStrategy;
import com.bigcompany.strategy.SalaryValidationStrategy;
import com.bigcompany.strategy.ValidationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeAnalyzerTest {

    private EmployeeAnalyzer employeeAnalyzer;
    private EmployeeFactory employeeFactory;
    private SalaryValidationStrategy salaryValidationStrategy;
    private ReportingValidationStrategy reportingValidationStrategy;

    @BeforeEach
    public void setUp() {
        employeeFactory = mock(EmployeeFactory.class);
        salaryValidationStrategy = mock(SalaryValidationStrategy.class);
        reportingValidationStrategy = mock(ReportingValidationStrategy.class);
        employeeAnalyzer = new EmployeeAnalyzer(employeeFactory,
                Arrays.asList(salaryValidationStrategy));
    }

    @Test
    public void testAnalyzeWithValidData() {

        Employee manager = new Employee.Builder()
                .setId(1)
                .setFirstName("John")
                .setLastName("Doe")
                .setSalary(80000)
                .build();

        Employee employee1 = new Employee.Builder()
                .setId(2)
                .setFirstName("Jane")
                .setLastName("Smith")
                .setSalary(60000)
                .setManagerId(1)
                .build();

        Employee employee2 = new Employee.Builder()
                .setId(3)
                .setFirstName("Jim")
                .setLastName("Brown")
                .setSalary(50000)
                .setManagerId(1)
                .build();

        List<Employee> employees = Arrays.asList(manager, employee1, employee2);
        when(employeeFactory.createEmployee(any())).thenReturn(manager, employee1, employee2);

        List<String[]> csvData = Arrays.asList(
                new String[]{"1", "John", "Doe", "80000", ""},
                new String[]{"2", "Jane", "Smith", "60000", "1"},
                new String[]{"3", "Jim", "Brown", "50000", "1"}
        );

        employeeAnalyzer.analyze(csvData);
        ArgumentCaptor<Employee> managerCaptor = ArgumentCaptor.forClass(Employee.class);
        ArgumentCaptor<List<Employee>> subordinatesCaptor = ArgumentCaptor.forClass(List.class);
        verify(salaryValidationStrategy, times(1)).validate(managerCaptor.capture(), subordinatesCaptor.capture());
        assertEquals(manager, managerCaptor.getValue());
        assertEquals(2, subordinatesCaptor.getValue().size());
        assertTrue(subordinatesCaptor.getValue().contains(employee1));
        assertTrue(subordinatesCaptor.getValue().contains(employee2));
    }

    @Test
    void testAnalyzeWithEmptyData() {
        List<String[]> emptyCsvData = Collections.emptyList();
        employeeAnalyzer.analyze(emptyCsvData);
        verifyNoInteractions(salaryValidationStrategy);
        verifyNoInteractions(reportingValidationStrategy);
    }

    @Test
    void testAnalyzeWithNoManagers() {
        List<String[]> csvData = List.of(
                new String[]{"1", "John", "Doe", "80000", null},
                new String[]{"2", "Jane", "Smith", "75000", null}
        );

        List<Employee> employees = List.of(
                new Employee.Builder().setId(1).setFirstName("John").setLastName("Doe").setSalary(80000).setManagerId(null).build(),
                new Employee.Builder().setId(2).setFirstName("Jane").setLastName("Smith").setSalary(75000).setManagerId(null).build()
        );
        employeeAnalyzer.analyze(csvData);
        verifyNoInteractions(salaryValidationStrategy, reportingValidationStrategy);
        verify(reportingValidationStrategy, times(1)).validate(eq(employees), anyMap());
    }

    @Test
    void testAnalyzeWithValidationStrategyException() {

        List<String[]> csvData = List.of(
                new String[]{"1", "John", "Doe", "80000", null},
                new String[]{"2", "Jane", "Smith", "75000", "1"}
        );

        doThrow(new RuntimeException("Validation Error"))
                .when(salaryValidationStrategy).validate(any(), anyList());
        employeeAnalyzer.analyze(csvData);
        verify(salaryValidationStrategy, times(1)).validate(any(), anyList());
        verify(reportingValidationStrategy, times(1)).validate(any(), anyList());
        verify(reportingValidationStrategy, times(1)).validate(anyList(), anyMap());
    }

    @Test
    void testAnalyzeWithSharedSubordinates() {

        List<String[]> csvData = List.of(
                new String[]{"1", "Manager1", "Boss", "100000", null},
                new String[]{"2", "Manager2", "Boss", "110000", null},
                new String[]{"3", "Employee1", "Worker", "50000", "1"},
                new String[]{"4", "Employee2", "Worker", "52000", "2"}
        );
        employeeAnalyzer.analyze(csvData);

        verify(salaryValidationStrategy, times(1)).validate(any(Employee.class), anyList());
        verify(reportingValidationStrategy, times(1)).validate(any(Employee.class), anyList());
        verify(reportingValidationStrategy, times(1)).validate(anyList(), anyMap());
    }



}

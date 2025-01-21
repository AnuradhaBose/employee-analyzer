package com.bigcompany;

import com.bigcompany.model.Employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeeAnalyzer {
    private final EmployeeFactory employeeFactory;
    private final List<ValidationStrategy> validationStrategies;

    public EmployeeAnalyzer(EmployeeFactory employeeFactory, List<ValidationStrategy> validationStrategies) {
        this.employeeFactory = employeeFactory;
        this.validationStrategies = validationStrategies;
    }

    public void analyze(List<String[]> csvData) {
        Map<Integer, Employee> employeeMap = new HashMap<>();
        List<Employee> employees = new ArrayList<>();

        for (String[] record : csvData) {
            Employee employee = employeeFactory.createEmployee(record);
            employees.add(employee);
            employeeMap.put(employee.getId(), employee);
        }

        validateSalaries(employees, employeeMap);
    }

    private void validateSalaries(List<Employee> employees, Map<Integer, Employee> employeeMap) {

        Map<Integer, List<Employee>> managerToSubordinates = employees.stream()
                .filter(emp -> emp.getManagerId() != null)
                .collect(Collectors.groupingBy(Employee::getManagerId));

        for (Map.Entry<Integer, List<Employee>> entry : managerToSubordinates.entrySet()) {
            Integer managerId = entry.getKey();
            List<Employee> subordinates = entry.getValue();


            Employee manager = employeeMap.get(managerId);
            if (manager != null) {

                validationStrategies.forEach(strategy -> strategy.validate(manager, subordinates));
            } else {

                System.err.println("Manager with ID " + managerId + " not found in the employee map.");
                System.err.println("Subordinates for missing manager: ");
                subordinates.forEach(sub ->
                        System.err.println(" - Employee ID: " + sub.getId() + ", Name: " + sub.getFirstName() + " " + sub.getLastName())
                );
            }
        }
        findEmployeesWithLongReportingLines(employees, employeeMap);
    }

    private void findEmployeesWithLongReportingLines(List<Employee> employees, Map<Integer, Employee> employeeMap) {

        for (Employee employee : employees) {
            int reportingLineLength = getReportingLineLength(employee, employeeMap);
            if (reportingLineLength > 4) {
                System.out.printf("Employee %s (ID: %d) has a reporting line %d levels long.%n",
                        employee.getFirstName(), employee.getId(), reportingLineLength);
            }
        }
    }
    private int getReportingLineLength(Employee employee, Map<Integer, Employee> employeeMap) {
        int length = 0;
        Integer managerId = employee.getManagerId();
        while (managerId != null) {
            length++;
            Employee manager = employeeMap.get(managerId);
            if (manager == null) {

                System.err.println("Manager with ID " + managerId + " not found for employee " + employee.getFirstName());
                break;
            }
            managerId = manager.getManagerId();
        }
        return length;
    }
}

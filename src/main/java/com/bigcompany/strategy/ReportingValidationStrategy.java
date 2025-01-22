package com.bigcompany.strategy;

import com.bigcompany.model.Employee;

import java.util.List;
import java.util.Map;

public class ReportingValidationStrategy implements ValidationStrategy {
    @Override
    public void validate(Employee manager, List<Employee> subordinates) {

    }

    public void validate(List<Employee> employees, Map<Integer, Employee> employeeMap){
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

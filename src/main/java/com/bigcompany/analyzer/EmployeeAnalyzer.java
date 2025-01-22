package com.bigcompany.analyzer;

import com.bigcompany.factory.EmployeeFactory;
import com.bigcompany.model.Employee;
import com.bigcompany.strategy.ReportingValidationStrategy;
import com.bigcompany.strategy.ValidationStrategy;

import java.util.Collections;
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
        try{
        List<Employee> employees = parseEmployees(csvData);
        Map<Integer, Employee> employeeMap = Collections.synchronizedMap(employees.stream()
                .collect(Collectors.toMap(Employee::getId, emp -> emp)));


        Map<Integer, List<Employee>> managerToSubordinates = Collections.synchronizedMap(employees.stream()
                .filter(emp -> emp.getManagerId() != null)
                .collect(Collectors.groupingBy(Employee::getManagerId)));


        for (Map.Entry<Integer, List<Employee>> entry : managerToSubordinates.entrySet()) {
            Employee manager = employeeMap.get(entry.getKey());
            List<Employee> subordinates = entry.getValue();
            validationStrategies.forEach(strategy -> strategy.validate(manager, subordinates));
        }


        validationStrategies.stream()
                .filter(strategy -> strategy instanceof ReportingValidationStrategy)
                .map(strategy -> (ReportingValidationStrategy) strategy)
                .forEach(strategy -> strategy.validate(employees, employeeMap));
    }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    private List<Employee> parseEmployees(List<String[]> csvData) {
        EmployeeFactory employeeFactory = new EmployeeFactory();
        return csvData.stream()
                .map(employeeFactory::createEmployee)
                .collect(Collectors.toList());
    }
}

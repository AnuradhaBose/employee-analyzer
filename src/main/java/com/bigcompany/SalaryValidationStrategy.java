package com.bigcompany;

import com.bigcompany.model.Employee;

import java.util.List;

public class SalaryValidationStrategy implements ValidationStrategy{
    @Override
    public void validate(Employee manager, List<Employee> subordinates) {
        double avgSalary = subordinates.stream()
                .mapToInt(Employee::getSalary)
                .average()
                .orElse(0);
        double minSalary = avgSalary * 1.2;
        double maxSalary = avgSalary * 1.5;

        if (manager.getSalary() < minSalary) {
            System.out.printf("- Manager %s (ID: %d) earns %.2f less than the minimum required.%n",
                    manager.getFirstName(), manager.getId(), minSalary - manager.getSalary());
        }
        if (manager.getSalary() > maxSalary) {
            System.out.printf("- Manager %s (ID: %d) earns %.2f more than the maximum allowed.%n",
                    manager.getFirstName(), manager.getId(), manager.getSalary() - maxSalary);
        }
    }
}

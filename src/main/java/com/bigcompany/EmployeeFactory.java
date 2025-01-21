package com.bigcompany;
import com.bigcompany.model.Employee;

public class EmployeeFactory {
    public Employee createEmployee(String[] fields) {
        return new Employee.Builder()
                .setId(Integer.parseInt(fields[0]))
                .setFirstName(fields[1])
                .setLastName(fields[2])
                .setSalary(Integer.parseInt(fields[3]))
                .setManagerId(fields[4] == null || fields[4].isEmpty() ? null : Integer.parseInt(fields[4]))
                .build();
    }
}
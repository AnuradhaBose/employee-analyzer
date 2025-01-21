package com.bigcompany;

import com.bigcompany.model.Employee;

import java.util.List;

public interface ValidationStrategy {
    void validate(Employee manager, List<Employee> subordinates);
}

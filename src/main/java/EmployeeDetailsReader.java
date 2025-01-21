import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.bigcompany.model.Employee;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmployeeDetailsReader {
    public static void main(String args[]){
        String csvFile = "employeeList.csv"; // Path to your CSV file

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(
                EmployeeDetailsReader.class.getClassLoader().getResourceAsStream(csvFile)
        ))) {
            List<Employee> empList = Stream.generate(() -> {
                        try {
                            return csvReader.readNext();
                        } catch (IOException | CsvValidationException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .takeWhile(record -> record != null)
                    .skip(1) // Skip the header row
                    .map(record -> new Employee.Builder()
                            .setId(Integer.parseInt(record[0]))
                            .setFirstName(record[1])
                            .setLastName(record[2])
                            .setSalary(Integer.parseInt(record[3]))
                            .setManagerId(record[4] == null || record[4].isEmpty()
                                    ? null
                                    : Integer.parseInt(record[4]))
                            .build()
                    )
                    .collect(Collectors.toList());

            // Print the list of Person objects
            empList.forEach(System.out::println);
            // Create a map for quick lookup by ID
            Map<Integer, Employee> employeeMap = empList.stream()
                    .collect(Collectors.toMap(Employee::getId, emp -> emp));

            // Find managers who don't meet salary rules
            findManagersWithSalaryIssues(empList, employeeMap);

            // Find employees with reporting lines that are too long
            findEmployeesWithLongReportingLines(empList, employeeMap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void findManagersWithSalaryIssues(List<Employee> employees, Map<Integer, Employee> employeeMap) {
        System.out.println("Managers with salary issues:");
        Map<Integer, List<Employee>> managerToSubordinates = employees.stream()
                .filter(emp -> emp.getManagerId() != null)
                .collect(Collectors.groupingBy(Employee::getManagerId));

        for (Map.Entry<Integer, List<Employee>> entry : managerToSubordinates.entrySet()) {
            Employee manager = employeeMap.get(entry.getKey());
            List<Employee> subordinates = entry.getValue();

            double avgSubordinateSalary = subordinates.stream()
                    .mapToInt(Employee::getSalary)
                    .average()
                    .orElse(0);

            double minSalary = avgSubordinateSalary * 1.2;
            double maxSalary = avgSubordinateSalary * 1.5;

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

    private static void findEmployeesWithLongReportingLines(List<Employee> employees, Map<Integer, Employee> employeeMap) {
        System.out.println("Employees with reporting lines that are too long:");
        for (Employee employee : employees) {
            int reportingLineLength = getReportingLineLength(employee, employeeMap);
            if (reportingLineLength > 4) {
                System.out.printf("- Employee %s (ID: %d) has a reporting line %d levels long.%n",
                        employee.getFirstName(), employee.getId(), reportingLineLength);
            }
        }
    }

    private static int getReportingLineLength(Employee employee, Map<Integer, Employee> employeeMap) {
        int length = 0;
        Integer managerId = employee.getManagerId();
        while (managerId != null) {
            length++;
            Employee manager = employeeMap.get(managerId);
            managerId = manager == null ? null : manager.getManagerId();
        }
        return length;
    }
}

package com.bigcompany.model;

public class Employee {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final int salary;
    private final Integer managerId;

    public Employee(Builder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.salary = builder.salary;
        this.managerId = builder.managerId;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getSalary() {
        return salary;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public static class Builder{

        private int id;
        private String firstName;
        private String lastName;
        private int salary;
        private Integer managerId;



        public Builder setId(int id){
            this.id = id;
            return this;
        }

        public Builder setFirstName(String firstName){
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName){
            this.lastName = lastName;
            return this;
        }

        public Builder setSalary(int salary){
            this.salary = salary;
            return this;
        }

        public Builder setManagerId(Integer managerId){
            this.managerId = managerId;
            return this;
        }

        public Employee build(){
            return new Employee(this);
        }

    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", salary=" + salary +
                ", managerId=" + managerId +
                '}';
    }
}

package com.sample;

import java.util.List;

public class Department {

    private String name;
    
    private List<Employee> employees;

    public Department( String name, List<Employee> employees ) {
        super();
        this.name = name;
        this.employees = employees;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees( List<Employee> employees ) {
        this.employees = employees;
    }
    
    
}

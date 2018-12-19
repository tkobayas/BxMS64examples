package com.sample;

public class Person {

    private String[] names;

    private int age;

    public Person() {
    }
    
    

    
    public Person(String[] names, int age) {
        super();
        this.names = names;
        this.age = age;
    }




    public String[] getNames() {
        return names;
    }

    
    public void setNames(String[] names) {
        this.names = names;
    }

    
    public int getAge() {
        return age;
    }

    
    public void setAge(int age) {
        this.age = age;
    }



}

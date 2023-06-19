package com.example.manakos.models;


public class Rate {
    private Integer id;
    private String name;
    private Float value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Rate(Integer id, String name, Float value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public Rate() {
    }
}

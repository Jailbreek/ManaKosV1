package com.example.manakos.models;

public class Indication {
    private Integer id;
    private String period;
    private Integer value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getId_counter() {
        return id_counter;
    }

    public void setId_counter(Integer id_counter) {
        this.id_counter = id_counter;
    }

    public Indication(Integer id, String period, Integer value, Integer id_counter) {
        this.id = id;
        this.period = period;
        this.value = value;
        this.id_counter = id_counter;
    }

    public Indication() {
    }

    private Integer id_counter;
}

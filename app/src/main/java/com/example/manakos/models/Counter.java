package com.example.manakos.models;

public class Counter {
    private Integer id;
    private String type;
    private String number;
    private Boolean used;
    private Integer id_flat;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public Integer getId_flat() {
        return id_flat;
    }

    public void setId_flat(Integer id_flat) {
        this.id_flat = id_flat;
    }

    public Counter(Integer id, String type, String number, Boolean used, Integer id_flat) {
        this.id = id;
        this.type = type;
        this.number = number;
        this.used = used;
        this.id_flat = id_flat;
    }

    public Counter() {
    }
}

package com.example.manakos.models;

public class Flat {
    private Integer id;
    private String personal_account;
    private String flat_number;
    private float total_area;
    private float usable_area;
    private String entrance_number;
    private String number_of_rooms;
    private int number_of_registered_residents;
    private int number_of_owners;
    private Integer id_tenant;

    public Flat() {
    }

    public Flat(Integer id, String personal_account, String flat_number, float total_area, float usable_area, String entrance_number, String number_of_rooms, int number_of_registered_residents, int number_of_owners, Integer id_tenant) {
        this.id = id;
        this.personal_account = personal_account;
        this.flat_number = flat_number;
        this.total_area = total_area;
        this.usable_area = usable_area;
        this.entrance_number = entrance_number;
        this.number_of_rooms = number_of_rooms;
        this.number_of_registered_residents = number_of_registered_residents;
        this.number_of_owners = number_of_owners;
        this.id_tenant = id_tenant;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPersonal_account() {
        return personal_account;
    }

    public void setPersonal_account(String personal_account) {
        this.personal_account = personal_account;
    }

    public String getFlat_number() {
        return flat_number;
    }

    public void setFlat_number(String flat_number) {
        this.flat_number = flat_number;
    }

    public float getTotal_area() {
        return total_area;
    }

    public void setTotal_area(float total_area) {
        this.total_area = total_area;
    }

    public float getUsable_area() {
        return usable_area;
    }

    public void setUsable_area(float usable_area) {
        this.usable_area = usable_area;
    }

    public String getEntrance_number() {
        return entrance_number;
    }

    public void setEntrance_number(String entrance_number) {
        this.entrance_number = entrance_number;
    }

    public String getNumber_of_rooms() {
        return number_of_rooms;
    }

    public void setNumber_of_rooms(String number_of_rooms) {
        this.number_of_rooms = number_of_rooms;
    }

    public int getNumber_of_registered_residents() {
        return number_of_registered_residents;
    }

    public void setNumber_of_registered_residents(int number_of_registered_residents) {
        this.number_of_registered_residents = number_of_registered_residents;
    }

    public int getNumber_of_owners() {
        return number_of_owners;
    }

    public void setNumber_of_owners(int number_of_owners) {
        this.number_of_owners = number_of_owners;
    }

    public Integer getId_tenant() {
        return id_tenant;
    }

    public void setId_tenant(Integer id_tenant) {
        this.id_tenant = id_tenant;
    }
}

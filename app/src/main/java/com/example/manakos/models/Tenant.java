package com.example.manakos.models;

import java.time.LocalDate;

public class Tenant {
    private Integer id_tenant;
    private String full_name;
    private LocalDate date_of_registration;
    private String number_of_family_members;
    private String phone_number;
    private String email;

    public Integer getId_tenant() {
        return id_tenant;
    }

    public void setId_tenant(Integer id_tenant) {
        this.id_tenant = id_tenant;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public LocalDate getDate_of_registration() {
        return date_of_registration;
    }

    public void setDate_of_registration(LocalDate date_of_registration) {
        this.date_of_registration = date_of_registration;
    }

    public String getNumber_of_family_members() {
        return number_of_family_members;
    }

    public void setNumber_of_family_members(String number_of_family_members) {
        this.number_of_family_members = number_of_family_members;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Tenant() {
    }

    public Tenant(Integer id_tenant, String full_name, LocalDate date_of_registration, String number_of_family_members, String phone_number, String email, String password) {
        this.id_tenant = id_tenant;
        this.full_name = full_name;
        this.date_of_registration = date_of_registration;
        this.number_of_family_members = number_of_family_members;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
    }

    private String password;
}

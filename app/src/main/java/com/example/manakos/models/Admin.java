package com.example.manakos.models;

public class Admin {
    private Integer id;
    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Admin(Integer id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public Admin() {
    }

    private String password;
}

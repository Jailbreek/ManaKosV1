package com.example.manakos.models;

public class Payment {
    private Integer id;
    private String period;
    private Float amount;
    private String cheque;
    private Boolean status;
    private Integer id_flat;
    private Integer id_rate;
    private Integer id_normative;

    public Payment() {
    }

    public Payment(Integer id, String period, Float amount, String cheque, Boolean status, Integer id_flat, Integer id_rate, Integer id_normative) {
        this.id = id;
        this.period = period;
        this.amount = amount;
        this.cheque = cheque;
        this.status = status;
        this.id_flat = id_flat;
        this.id_rate = id_rate;
        this.id_normative = id_normative;
    }

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

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getCheque() {
        return cheque;
    }

    public void setCheque(String cheque) {
        this.cheque = cheque;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getId_flat() {
        return id_flat;
    }

    public void setId_flat(Integer id_flat) {
        this.id_flat = id_flat;
    }

    public Integer getId_rate() {
        return id_rate;
    }

    public void setId_rate(Integer id_rate) {
        this.id_rate = id_rate;
    }

    public Integer getId_normative() {
        return id_normative;
    }

    public void setId_normative(Integer id_normative) {
        this.id_normative = id_normative;
    }
}

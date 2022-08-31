package kr.co.tbell.echeck.model;

import java.io.Serializable;

public class Elect implements Serializable {

    private Long id;

    private String electAmount;

    private String electCharge;

    private String electMeasure;

    private String createdAt;

    public Elect() { }

    public Elect(Long id, String electAmount, String electCharge, String electMeasure, String createdAt) {
        this.id = id;
        this.electAmount = electAmount;
        this.electCharge = electCharge;
        this.electMeasure = electMeasure;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getElectAmount() {
        return electAmount;
    }

    public void setElectAmount(String electAmount) {
        this.electAmount = electAmount;
    }

    public String getElectCharge() {
        return electCharge;
    }

    public void setElectCharge(String electCharge) {
        this.electCharge = electCharge;
    }

    public String getElectMeasure() {
        return electMeasure;
    }

    public void setElectMeasure(String electMeasure) {
        this.electMeasure = electMeasure;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Elect{" +
                "id=" + id +
                ", electAmount='" + electAmount + '\'' +
                ", electCharge='" + electCharge + '\'' +
                ", electMeasure='" + electMeasure + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}

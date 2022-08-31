package kr.co.tbell.echeck.model.dto;

import java.io.Serializable;

public class HomeProduct implements Serializable {

    private Long id;

    private String product;

    private String usagePattern;

    private String dayHour;

    private String persentage;

    private String createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getUsagePattern() {
        return usagePattern;
    }

    public void setUsagePattern(String usagePattern) {
        this.usagePattern = usagePattern;
    }

    public String getDayHour() {
        return dayHour;
    }

    public void setDayHour(String dayHour) {
        this.dayHour = dayHour;
    }

    public String getPersentage() {
        return persentage;
    }

    public void setPersentage(String persentage) {
        this.persentage = persentage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "HomeProduct{" +
                "id=" + id +
                ", product='" + product + '\'' +
                ", usagePattern='" + usagePattern + '\'' +
                ", dayHour='" + dayHour + '\'' +
                ", persentage='" + persentage + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}

package kr.co.tbell.echeck.model.dto;

import java.util.List;

import kr.co.tbell.echeck.model.House;

public class Calculator {

    private int electUsage;

    private String use;

    private String start;

    private String end;

    private String season;

    private List<House> houses;

    public int getElectUsage() {
        return electUsage;
    }

    public void setElectUsage(int electUsage) {
        this.electUsage = electUsage;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public List<House> getHouses() {
        return houses;
    }

    public void setHouses(List<House> houses) {
        this.houses = houses;
    }

    @Override
    public String toString() {
        return "Calculator{" +
                "electUsage=" + electUsage +
                ", use='" + use + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", season='" + season + '\'' +
                ", houses=" + houses +
                '}';
    }
}

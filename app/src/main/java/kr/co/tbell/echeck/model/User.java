package kr.co.tbell.echeck.model;

public class User {

    private Long id;

    private String nickname;

    private String electBefore;

    private String electPeriod;

    private String electUse;

    private String electHouse;

    private String houseCount;

    private String createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getElectBefore() {
        return electBefore;
    }

    public void setElectBefore(String electBefore) {
        this.electBefore = electBefore;
    }

    public String getElectPeriod() {
        return electPeriod;
    }

    public void setElectPeriod(String electPeriod) {
        this.electPeriod = electPeriod;
    }

    public String getElectUse() {
        return electUse;
    }

    public void setElectUse(String electUse) {
        this.electUse = electUse;
    }

    public String getElectHouse() {
        return electHouse;
    }

    public void setElectHouse(String electHouse) {
        this.electHouse = electHouse;
    }

    public String getHouseCount() {
        return houseCount;
    }

    public void setHouseCount(String houseCount) {
        this.houseCount = houseCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", electBefore='" + electBefore + '\'' +
                ", electPeriod='" + electPeriod + '\'' +
                ", electUse='" + electUse + '\'' +
                ", electHouse='" + electHouse + '\'' +
                ", houseCount='" + houseCount + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}

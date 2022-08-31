package kr.co.tbell.echeck.model.dto;

import java.io.Serializable;

public class InfoData implements Serializable {

    private String nickname;

    private String beforeElect;

    private String nowPeriod;

    private String useElect;

    private String houseCount;

    private Long userId;

    private Long currentHouseId;

    private int housePosition;

    private String houseType;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBeforeElect() {
        return beforeElect;
    }

    public void setBeforeElect(String beforeElect) {
        this.beforeElect = beforeElect;
    }

    public String getNowPeriod() {
        return nowPeriod;
    }

    public void setNowPeriod(String nowPeriod) {
        this.nowPeriod = nowPeriod;
    }

    public String getUseElect() {
        return useElect;
    }

    public void setUseElect(String useElect) {
        this.useElect = useElect;
    }

    public String getHouseCount() {
        return houseCount;
    }

    public void setHouseCount(String houseCount) {
        this.houseCount = houseCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getHouseType() {
        return houseType;
    }

    public void setHouseType(String houseType) {
        this.houseType = houseType;
    }

    public Long getCurrentHouseId() {
        return currentHouseId;
    }

    public void setCurrentHouseId(Long currentHouseId) {
        this.currentHouseId = currentHouseId;
    }

    public int getHousePosition() {
        return housePosition;
    }

    public void setHousePosition(int housePosition) {
        this.housePosition = housePosition;
    }

    @Override
    public String toString() {
        return "InfoData{" +
                "nickname='" + nickname + '\'' +
                ", beforeElect='" + beforeElect + '\'' +
                ", nowPeriod='" + nowPeriod + '\'' +
                ", useElect='" + useElect + '\'' +
                ", houseCount='" + houseCount + '\'' +
                ", userId=" + userId +
                ", currentHouseId=" + currentHouseId +
                ", housePosition=" + housePosition +
                ", houseType='" + houseType + '\'' +
                '}';
    }
}

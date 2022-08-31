package kr.co.tbell.echeck.model;

import java.util.List;

public class HouseDiscount {

    private int houseNum;

    private List<String> discountList;

    public int getHouseNum() {
        return houseNum;
    }

    public void setHouseNum(int houseNum) {
        this.houseNum = houseNum;
    }

    public List<String> getDiscountList() {
        return discountList;
    }

    public void setDiscountList(List<String> discountList) {
        this.discountList = discountList;
    }

    @Override
    public String toString() {
        return "HouseDiscount{" +
                "houseNum=" + houseNum +
                ", discountList=" + discountList +
                '}';
    }
}

package kr.co.tbell.echeck.model;

public class House {

    private Long id;

    private String houseDiscountYn;

    private String houseDiscount1;

    private String houseDiscount2;

    private Long userHouseId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHouseDiscountYn() {
        return houseDiscountYn;
    }

    public void setHouseDiscountYn(String houseDiscountYn) {
        this.houseDiscountYn = houseDiscountYn;
    }

    public String getHouseDiscount1() {
        return houseDiscount1;
    }

    public void setHouseDiscount1(String houseDiscount1) {
        this.houseDiscount1 = houseDiscount1;
    }

    public String getHouseDiscount2() {
        return houseDiscount2;
    }

    public void setHouseDiscount2(String houseDiscount2) {
        this.houseDiscount2 = houseDiscount2;
    }

    public Long getUserHouseId() {
        return userHouseId;
    }

    public void setUserHouseId(Long userHouseId) {
        this.userHouseId = userHouseId;
    }

    @Override
    public String toString() {
        return "House{" +
                "id=" + id +
                ", houseDiscountYn='" + houseDiscountYn + '\'' +
                ", houseDiscount1='" + houseDiscount1 + '\'' +
                ", houseDiscount2='" + houseDiscount2 + '\'' +
                ", userHouseId=" + userHouseId +
                '}';
    }
}

package kr.co.tbell.echeck.model.dto;

public class DiscountCount {

    private String type;

    private String discountName;

    private int count;

    public DiscountCount(String type, String discountName, int count) {
        this.type = type;
        this.discountName = discountName;
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "DiscountCount{" +
                "discountName='" + discountName + '\'' +
                ", count=" + count +
                '}';
    }
}

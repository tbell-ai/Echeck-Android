package kr.co.tbell.echeck.model.dto;

public class HouseItem {
    private String titleText;

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    @Override
    public String toString() {
        return "HouseItemList{" +
                "titleText='" + titleText + '\'' +
                '}';
    }
}

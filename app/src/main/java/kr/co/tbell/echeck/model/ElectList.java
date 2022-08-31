package kr.co.tbell.echeck.model;

public class ElectList {

    private int thumbImg;

    private String titleText;

    private String detailText;

    public ElectList(int thumbImg, String titleText, String detailText) {
        this.thumbImg = thumbImg;
        this.titleText = titleText;
        this.detailText = detailText;
    }

    public int getThumbImg() {
        return thumbImg;
    }

    public void setThumbImg(int thumbImg) {
        this.thumbImg = thumbImg;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getDetailText() {
        return detailText;
    }

    public void setDetailText(String detailText) {
        this.detailText = detailText;
    }

    @Override
    public String toString() {
        return "ElectList{" +
                "thumbImg=" + thumbImg +
                ", titleText='" + titleText + '\'' +
                ", detailText='" + detailText + '\'' +
                '}';
    }
}

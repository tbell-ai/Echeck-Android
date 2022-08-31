package kr.co.tbell.echeck.model.dto;

public class ElectDialogList {

    private int thumbImg;

    private String title;

    private String usage;

    private String charge;

    public ElectDialogList(int thumbImg, String title, String usage, String charge) {
        this.thumbImg = thumbImg;
        this.title = title;
        this.usage = usage;
        this.charge = charge;
    }

    public int getThumbImg() {
        return thumbImg;
    }

    public void setThumbImg(int thumbImg) {
        this.thumbImg = thumbImg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    @Override
    public String toString() {
        return "ElectDialogList{" +
                "thumbImg=" + thumbImg +
                ", title='" + title + '\'' +
                ", usage='" + usage + '\'' +
                ", charge='" + charge + '\'' +
                '}';
    }
}

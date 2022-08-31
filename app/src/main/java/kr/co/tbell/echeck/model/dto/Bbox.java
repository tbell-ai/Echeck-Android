package kr.co.tbell.echeck.model.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Bbox implements Serializable {

    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("num_seq")
    @Expose
    private String numSeq;
    @SerializedName("width")
    @Expose
    private String width;
    @SerializedName("x")
    @Expose
    private String x;
    @SerializedName("y")
    @Expose
    private String y;

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getNumSeq() {
        return numSeq;
    }

    public void setNumSeq(String numSeq) {
        this.numSeq = numSeq;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Bbox{" +
                "height='" + height + '\'' +
                ", numSeq='" + numSeq + '\'' +
                ", width='" + width + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                '}';
    }
}

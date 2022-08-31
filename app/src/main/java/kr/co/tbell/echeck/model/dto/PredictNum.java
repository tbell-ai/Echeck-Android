package kr.co.tbell.echeck.model.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PredictNum implements Serializable {

    @SerializedName("confidence")
    @Expose
    private String confidence;
    @SerializedName("num_seq")
    @Expose
    private String numSeq;
    @SerializedName("predict")
    @Expose
    private String predict;

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getNumSeq() {
        return numSeq;
    }

    public void setNumSeq(String numSeq) {
        this.numSeq = numSeq;
    }

    public String getPredict() {
        return predict;
    }

    public void setPredict(String predict) {
        this.predict = predict;
    }

    @Override
    public String toString() {
        return "PredictNum{" +
                "confidence='" + confidence + '\'' +
                ", numSeq='" + numSeq + '\'' +
                ", predict='" + predict + '\'' +
                '}';
    }
}

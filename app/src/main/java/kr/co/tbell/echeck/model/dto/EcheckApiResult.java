package kr.co.tbell.echeck.model.dto;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EcheckApiResult implements Serializable {

    @SerializedName("bbox")
    @Expose
    private List<Bbox> bbox = null;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("predict_num")
    @Expose
    private List<PredictNum> predictNum = null;
    @SerializedName("request_time")
    @Expose
    private String requestTime;
    @SerializedName("response_code")
    @Expose
    private String responseCode;

    public List<Bbox> getBbox() {
        return bbox;
    }

    public void setBbox(List<Bbox> bbox) {
        this.bbox = bbox;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<PredictNum> getPredictNum() {
        return predictNum;
    }

    public void setPredictNum(List<PredictNum> predictNum) {
        this.predictNum = predictNum;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public String toString() {
        return "EcheckApiResult{" +
                "bbox=" + bbox +
                ", deviceId='" + deviceId + '\'' +
                ", predictNum=" + predictNum +
                ", requestTime='" + requestTime + '\'' +
                ", responseCode='" + responseCode + '\'' +
                '}';
    }
}

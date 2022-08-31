package kr.co.tbell.echeck.model.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EcheckRequest implements Serializable {

    @SerializedName("device_id")
    @Expose
    private String deviceId;

    @SerializedName("request_time")
    @Expose
    private String requestTime;

    @SerializedName("meter_id")
    @Expose
    private String meterId;

    @SerializedName("imBytearray")
    @Expose
    private String imBytearray;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    public String getImBytearray() {
        return imBytearray;
    }

    public void setImBytearray(String imBytearray) {
        this.imBytearray = imBytearray;
    }

    @Override
    public String toString() {
        return "EcheckRequest{" +
                "deviceId='" + deviceId + '\'' +
                ", requestTime='" + requestTime + '\'' +
                ", meterId='" + meterId + '\'' +
                ", imBytearray='" + imBytearray + '\'' +
                '}';
    }
}

package org.gemalto.com.uaf;

/**
 * Created by jpaert on 9/6/2017.
 */
public class AuthenticatorRecord {

    private static final String DLM = "#";

    private String aaid;
    private String keyID;
    private String deviceId;
    private String userID;
    private String status;

    public AuthenticatorRecord() {
    }

    public String getAaid() {
        return aaid;
    }

    public void setAaid(String aaid) {
        this.aaid = aaid;
    }

    public String getKeyID() {
        return keyID;
    }

    public void setKeyID(String keyID) {
        this.keyID = keyID;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

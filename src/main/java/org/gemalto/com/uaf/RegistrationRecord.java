package org.gemalto.com.uaf;

/**
 * Created by jpaert on 9/6/2017.
 */
public class RegistrationRecord {

    // private final AuthenticatorRecord authenticator;
    private String publicKey;
    private String signCounter;
    private String authenticatorVersion;
    private String tcDisplayPNGCharacteristics;
    private String username;
    private String deviceId;
    private String timeStamp;
    private String status;
    private String attestCert;
    private String attestDataToSign;
    private String attestSignature;
    private String attestVerifiedStatus;


    public RegistrationRecord() {

    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getSignCounter() {
        return signCounter;
    }

    public void setSignCounter(String signCounter) {
        this.signCounter = signCounter;
    }

    public String getAuthenticatorVersion() {
        return authenticatorVersion;
    }

    public void setAuthenticatorVersion(String authenticatorVersion) {
        this.authenticatorVersion = authenticatorVersion;
    }

    public String getTcDisplayPNGCharacteristics() {
        return tcDisplayPNGCharacteristics;
    }

    public void setTcDisplayPNGCharacteristics(String tcDisplayPNGCharacteristics) {
        this.tcDisplayPNGCharacteristics = tcDisplayPNGCharacteristics;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttestCert() {
        return attestCert;
    }

    public void setAttestCert(String attestCert) {
        this.attestCert = attestCert;
    }

    public String getAttestDataToSign() {
        return attestDataToSign;
    }

    public void setAttestDataToSign(String attestDataToSign) {
        this.attestDataToSign = attestDataToSign;
    }

    public String getAttestSignature() {
        return attestSignature;
    }

    public void setAttestSignature(String attestSignature) {
        this.attestSignature = attestSignature;
    }

    public String getAttestVerifiedStatus() {
        return attestVerifiedStatus;
    }

    public void setAttestVerifiedStatus(String attestVerifiedStatus) {
        this.attestVerifiedStatus = attestVerifiedStatus;
    }

    public String getUsername() {
        return username;
    }

    public RegistrationRecord setUsername(String username) {
        this.username = username;
        return this;
    }
}
package org.gemalto.com.uaf;

/**
 * Created by jpaert on 9/6/2017.
 */
public enum UafStatusCode {


    Success(1200),
    Accepted(1202),
    BadRequest(1400),
    Unauthorized(1403),
    NotFound(1404),
    RequestTimeout(1408),
    UnknownAaid(1480),
    UnkownKeyId(1481),
    ChannelBindingRefused(1490),
    RequestInvalid(1491),
    UnacceptableAuthenticator(1492),
    RevokedAuthenticator(1493),
    UnacceptableKey(1494),
    UnacceptableAlgorithm(1495),
    UnacceptableAttestation(1496),
    UnacceptableClientCapabilities(1497),
    UnacceptableContent(1498),
    ServerError(1500);


    private final int code;

    UafStatusCode(int code) {
        this.code = code;
    }

    public static UafStatusCode fromStatus(final String uafServerStatus) {
        if (UafServerStatus.SUCCESS.getLabel().equals(uafServerStatus)) {
            return UafStatusCode.Success;
        } else if (UafServerStatus.ASSERTIONS_CHECK_FAILED.getLabel().equals(uafServerStatus)) {
            return UafStatusCode.UnacceptableAttestation;
        } else if (UafServerStatus.INVALID_SERVER_DATA_EXPIRED.getLabel().equals(uafServerStatus)
                || UafServerStatus.INVALID_SERVER_DATA_SIGNATURE_NO_MATCH.getLabel().equals(uafServerStatus)
                || UafServerStatus.INVALID_SERVER_DATA_CHECK_FAILED.getLabel().equals(uafServerStatus)) {
            return UafStatusCode.RequestInvalid;
        } else {
            return UafStatusCode.UnacceptableContent;
        }
    }

    public int getCode() {
        return code;
    }
}

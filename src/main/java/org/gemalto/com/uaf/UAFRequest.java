package org.gemalto.com.uaf;

import org.gemalto.com.uaf.spec_v1_1.ReturnUAFRequest;

import javax.ws.rs.core.Response;

/**
 * Created by jpaert on 9/5/2017.
 */
public class UAFRequest {

    private static final int FIDO_CODE_STATUS_MODIFIER = 1000;

    private String uafRequest;
    private int statusCode;

    public static UAFRequest fromResponse(GetUAFRequest req, Response response) {
        if (req.getOperation() == Operation.Auth) {
            return createBasedOnSpecialAuthCaseForCertification(response);
        }
        final UAFRequest returnUAFRequest = new UAFRequest();
        returnUAFRequest.setStatusCode(FIDO_CODE_STATUS_MODIFIER + response.getStatus());
        returnUAFRequest.setUafRequest(response.readEntity(String.class));
        return returnUAFRequest;
    }

    private static UAFRequest createBasedOnSpecialAuthCaseForCertification(Response response) {
        final UAFRequest returnUAFRequest = new UAFRequest();
        final String payload = response.readEntity(String.class);
        if (payload .replaceAll("\\s", "").contains("\"accepted\":[]")) {
            returnUAFRequest.setStatusCode(1404);
            returnUAFRequest.setUafRequest("No authenticator was found.");
        } else {
            returnUAFRequest.setStatusCode(FIDO_CODE_STATUS_MODIFIER + response.getStatus());
            returnUAFRequest.setUafRequest(payload);
        }
        return returnUAFRequest;
    }

    public String getUafRequest() {
        return uafRequest;
    }

    public void setUafRequest(String uafRequest) {
        this.uafRequest = uafRequest;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

}

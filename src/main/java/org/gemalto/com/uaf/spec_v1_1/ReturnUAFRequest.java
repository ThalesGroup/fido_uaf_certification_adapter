package org.gemalto.com.uaf.spec_v1_1;

import org.gemalto.com.uaf.GetUAFRequest;
import org.gemalto.com.uaf.Operation;

import javax.ws.rs.core.Response;

/**
 * Created by drurenia on 2/13/2018.
 */
public class ReturnUAFRequest {

    private static final int FIDO_CODE_STATUS_MODIFIER = 1000;

    private long statusCode;

    private String uafRequest;

    private Operation op;

    private long lifetimeMillis;

    public static ReturnUAFRequest fromResponse(GetUAFRequest req, Response response) {
        if (req.getOperation() == Operation.Auth) {
            return createBasedOnSpecialAuthCaseForCertification(response);
        }
        final ReturnUAFRequest returnUAFRequest = new ReturnUAFRequest();
        returnUAFRequest.setStatusCode(FIDO_CODE_STATUS_MODIFIER + response.getStatus());
        returnUAFRequest.setOp(req.getOperation());
        returnUAFRequest.setUafRequest(response.readEntity(String.class));
        return returnUAFRequest;
    }

    private static ReturnUAFRequest createBasedOnSpecialAuthCaseForCertification(Response response) {
        final ReturnUAFRequest returnUAFRequest = new ReturnUAFRequest();
        returnUAFRequest.setOp(Operation.Auth);
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

    public long getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(long statusCode) {
        this.statusCode = statusCode;
    }

    public String getUafRequest() {
        return uafRequest;
    }

    public void setUafRequest(String uafRequest) {
        this.uafRequest = uafRequest;
    }

    public Operation getOp() {
        return op;
    }

    public void setOp(Operation op) {
        this.op = op;
    }

    public long getLifetimeMillis() {
        return lifetimeMillis;
    }

    public void setLifetimeMillis(long lifetimeMillis) {
        this.lifetimeMillis = lifetimeMillis;
    }
}

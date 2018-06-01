package org.gemalto.com.uaf.spec_v1_0;

import org.gemalto.com.uaf.Context;
import org.gemalto.com.uaf.GetUAFRequest;
import org.gemalto.com.uaf.Operation;
import org.gemalto.com.uaf.UafAdapterSettings;

/**
 * Created by jpaert on 9/5/2017.
 */
public class GetUAFRequestV1_0 implements GetUAFRequest {

    private Operation op;
    private String previousRequest;
    private ContextV1_0 context;

    @Override
    public String resolveUrl(String appId) {
        if (this.getOperation() == Operation.Auth) {
            return resolveAuthenticationUrl(appId);
        } else if (this.getOperation() == Operation.Reg) {
            return UafAdapterSettings.getRegistrationRequestPath() + this.getContext().getUsername() +  "?appId=" + appId;
        } else if (this.getOperation() == Operation.Dereg) {
            return UafAdapterSettings.getDeregistrationRequestPath() + this.getContext().getUsername() + "?appId=" + appId;
        }
        return null;
    }

    private String resolveAuthenticationUrl(final String appId) {
        final String appIdQueryParam = "?appId=" + appId;
        final String authRequestWithoutTransaction =
                UafAdapterSettings.getAuthenticationRequestPath() + "/" + this.getContext().getUsername();
        if (this.getContext().hasTransaction()) {
            return authRequestWithoutTransaction + "/" + this.getContext().getTransaction() + appIdQueryParam;
        } else {
            return authRequestWithoutTransaction + appIdQueryParam;
        }
    }


    public Operation getOperation() {
        return op;
    }

    public void setOp(Operation op) {
        this.op = op;
    }

    public String getPreviousRequest() {
        return previousRequest;
    }

    public void setPreviousRequest(String previousRequest) {
        this.previousRequest = previousRequest;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(ContextV1_0 context) {
        this.context = context;
    }

}

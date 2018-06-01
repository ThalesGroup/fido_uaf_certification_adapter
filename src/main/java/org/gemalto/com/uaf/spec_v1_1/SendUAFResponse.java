package org.gemalto.com.uaf.spec_v1_1;

import org.gemalto.com.uaf.Operation;

/**
 * Created by drurenia on 2/13/2018.
 */
public class SendUAFResponse {

    private long statusCode;

    private String uafRequest;

    private Operation op;

    private long lifetimeMillis;

    public long getStatusCode() {
        return statusCode;
    }

    public String getUafRequest() {
        return uafRequest;
    }

    public Operation getOp() {
        return op;
    }

    public long getLifetimeMillis() {
        return lifetimeMillis;
    }
}

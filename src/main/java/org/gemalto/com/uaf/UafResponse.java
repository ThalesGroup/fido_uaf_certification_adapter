package org.gemalto.com.uaf;

/**
 * Created by jpaert on 9/6/2017.
 */
public class UafResponse {

    private String uafResponse;
    private int statusCode;

    public String getUafResponse() {
        return uafResponse;
    }

    public void setUafResponse(String uafResponse) {
        this.uafResponse = uafResponse;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;


    }

}

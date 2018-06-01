package org.gemalto.com.uaf;

/**
 * Created by jpaert on 9/5/2017.
 */
public interface GetUAFRequest {

    Operation getOperation();

    Context getContext();

    String getPreviousRequest();

    String resolveUrl(String appId);
}

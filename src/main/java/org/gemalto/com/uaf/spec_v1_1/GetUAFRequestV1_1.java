package org.gemalto.com.uaf.spec_v1_1;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.gemalto.com.uaf.Context;
import org.gemalto.com.uaf.GetUAFRequest;
import org.gemalto.com.uaf.Operation;
import org.gemalto.com.uaf.UafAdapterSettings;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by drurenia on 2/13/2018.
 */
public class GetUAFRequestV1_1 implements GetUAFRequest {

    private Operation op;

    private String previousRequest;

    private String context;

    @Override
    public String resolveUrl(final String appId) {
        if (this.getOperation() == Operation.Auth) {
            return resolveAuthenticationUrl(appId);
        } else if (this.getOperation() == Operation.Reg) {
            return UafAdapterSettings.getRegistrationRequestPath() + this.getContext().getUsername() + "?appId=" + appId;
        } else if (this.getOperation() == Operation.Dereg) {
            return resolveDeregistrationUrl(appId);
        }
        return null;
    }

    private String resolveDeregistrationUrl(final String appId) {
        final ContextV1_1 context = (ContextV1_1) this.getContext();

        if (StringUtils.isBlank(context.getDeregisterAAID())) {
            return UafAdapterSettings.getDeregistrationRequestPath() + this.getContext().getUsername() + "?appId=" + appId + "&deregisterAll=" + context.isDeregisterAll();
        } else {
            final String deregisterAAID = context.getDeregisterAAID();
            try {
                return UafAdapterSettings.getDeregistrationRequestPath() + this.getContext().getUsername() + "?appId=" + appId + "&deregisterAll=" + context.isDeregisterAll() + "&aaid=" + URLEncoder.encode(deregisterAAID, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("A problem happened while URL encoding the AAID");
            }
        }
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

    @Override
    public Operation getOperation() {
        return op;
    }

    @Override
    public Context getContext() {
        Gson gson = new Gson();
        return gson.fromJson(context, ContextV1_1.class);
    }

    @Override
    public String getPreviousRequest() {
        return previousRequest;
    }
}

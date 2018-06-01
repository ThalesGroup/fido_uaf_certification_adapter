package org.gemalto.com.uaf.spec_v1_1;

import org.apache.commons.lang3.StringUtils;
import org.gemalto.com.uaf.Context;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Created by jpaert on 9/5/2017.
 */
public class ContextV1_1 implements Context {

    private String username;

    private String transaction;

    private String deregisterAAID;

    private boolean deregisterAll;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getTransaction() {
        return Base64.getEncoder()
                .withoutPadding()
                .encodeToString(this.transaction.getBytes(Charset.forName("UTF-8")));
    }

    @Override
    public boolean hasTransaction() {
        return StringUtils.isNotEmpty(this.transaction);
    }

    public String getDeregisterAAID() {
        return deregisterAAID;
    }

    public boolean isDeregisterAll() {
        return deregisterAll;
    }
}

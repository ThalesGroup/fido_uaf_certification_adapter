package org.gemalto.com.uaf.spec_v1_0;

import org.apache.commons.lang3.StringUtils;
import org.gemalto.com.uaf.Context;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Created by jpaert on 9/5/2017.
 */
public class ContextV1_0 implements Context {

    private String userName;

    private String transaction;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean hasTransaction() {
        return StringUtils.isNotEmpty(transaction);
    }

    public String getTransaction() {
        return Base64.getEncoder()
                .withoutPadding()
                .encodeToString(this.transaction.getBytes(Charset.forName("UTF-8")));
    }

    public Context setTransaction(String transaction) {
        this.transaction = transaction;
        return this;
    }

    @Override
    public String getUsername() {
        return userName;
    }
}

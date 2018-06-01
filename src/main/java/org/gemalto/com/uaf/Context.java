package org.gemalto.com.uaf;

/**
 * Created by jpaert on 9/5/2017.
 */
public interface Context {

    String getUsername();

    String getTransaction();

    boolean hasTransaction();
}

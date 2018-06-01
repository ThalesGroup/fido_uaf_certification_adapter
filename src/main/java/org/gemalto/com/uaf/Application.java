package org.gemalto.com.uaf;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * Created by drurenia on 2/13/2018.
 */
@ApplicationPath("/")
public class Application extends ResourceConfig {

    public Application() {
        packages("org.gemalto.com.uaf.spec_v1_0,org.gemalto.com.uaf.spec_v1_1");
    }
}

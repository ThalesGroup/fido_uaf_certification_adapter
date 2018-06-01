package org.gemalto.com.uaf;

import java.util.List;

/**
 * JWT authentication params specific for application
 */
public class JwtAuthenticationParams {

    private final String jwtTokenHeader;
    private final JwtSecret secret;
    private final String tenantIdParamKey;
    private final List<String> roles;

    public JwtAuthenticationParams(String jwtTokenHeader, JwtSecret secret, String tenantIdParamKey, List<String> roles) {
        this.jwtTokenHeader = jwtTokenHeader;
        this.secret = secret;
        this.tenantIdParamKey = tenantIdParamKey;
        this.roles = roles;
    }

    public String getJwtTokenHeader() {
        return jwtTokenHeader;
    }

    public JwtSecret getSecret() {
        return secret;
    }

    public String getTenantIdParamKey() {
        return tenantIdParamKey;
    }

    public List<String> getRoles() {
        return roles;
    }
}

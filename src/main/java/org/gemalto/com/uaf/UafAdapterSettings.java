package org.gemalto.com.uaf;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Created by jpaert on 8/28/2017.
 */
public class UafAdapterSettings {

    private static String uafServerDomainAndContext = "http://localhost/fidouaf";
    private static String registrationRequestPath = "/v1/public/regRequest/";
    private static String registrationResponsePath = "/v1/public/regResponse";
    private static String authenticationRequestPath = "/v1/public/authRequest";
    private static String authenticationResponsePath = "/v1/public/authResponse";
    private static String deregistrationRequestPath = "/v1/public/deregRequest/";
    private static String trustedFacetsPath = "/v1/public/uaf/facets";
    private static String facetsEndpoint = "/v11/facets";

    static {
        resolveVariables();
    }

    private static void resolveVariables() {
        String serverEnv = System.getenv("UAF_SERVER");
        if (StringUtils.isNotBlank(serverEnv)) {
            uafServerDomainAndContext = serverEnv;
        }

        String reqPathEnv = System.getenv("UAF_REGISTRATION_REQUEST_PATH");
        if (StringUtils.isNotBlank(reqPathEnv)) {
            if (!reqPathEnv.endsWith("/")) {
                reqPathEnv = reqPathEnv + "/";
            }
            registrationRequestPath = reqPathEnv;
        }

        String regRespPathEnv = System.getenv("UAF_REGISTRATION_RESPONSE_PATH");
        if (StringUtils.isNotBlank(regRespPathEnv)) {
            registrationResponsePath = regRespPathEnv;
        }

        String authReqPathEnv = System.getenv("UAF_AUTHENTICATION_REQUST_PATH");
        if (StringUtils.isNotBlank(authReqPathEnv)) {
            if (StringUtils.isNotBlank(authReqPathEnv)) {
                if (authReqPathEnv.endsWith("/")) {
                    authReqPathEnv = authReqPathEnv.substring(0, authReqPathEnv.length() - 1);
                }
            }
            authenticationRequestPath = authReqPathEnv;
        }

        String authRespPathEnv = System.getenv("UAF_AUTHENTICATION_RESPONSE_PATH");
        if (StringUtils.isNotBlank(authRespPathEnv)) {
            authenticationResponsePath = authRespPathEnv;
        }

        String trustedFacetsPathEnv = System.getenv("UAF_TRUSTED_FACETS_PATH");
        if (StringUtils.isNotBlank(trustedFacetsPathEnv)) {
            trustedFacetsPath = trustedFacetsPathEnv;
        }
    }

    public static String getRegistrationRequestPath() {
        return uafServerDomainAndContext + registrationRequestPath;
    }

    public static String getRegistrationResponsePath() {
        return uafServerDomainAndContext + registrationResponsePath;
    }

    public static String getAuthenticationRequestPath() {
        return uafServerDomainAndContext + authenticationRequestPath;
    }

    public static String getAuthenticationResponsePath() {
        return uafServerDomainAndContext + authenticationResponsePath;
    }

    public static String getUafInfoPath() {
        return uafServerDomainAndContext + "/v1/info";
    }

    public static String getUafServerDomainAndContext() {
        resolveVariables();
        return uafServerDomainAndContext;
    }

    public static String getDeregistrationRequestPath() {
        return uafServerDomainAndContext + deregistrationRequestPath;
    }

    public static String getTrustedFacetsPath() {
        return uafServerDomainAndContext + trustedFacetsPath;
    }

    public static String getFacetsEndpoint() {
        return facetsEndpoint;
    }
}

package org.gemalto.com.uaf;

import com.gemalto.security.jwt.JwtAuthenticationParams;
import com.gemalto.security.jwt.JwtSecret;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.gemalto.com.uaf.ResourceAccess.Roles;

import java.lang.reflect.Type;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT creation for tests
 */
public class JWTCreator {

    public static final String REQ_PARAM_TENANT_ID = "tenantKey";
    public static final String JWT_ATTR_TENANT_ID = "tenant";
    public static final String JWT_ATTR_PRINCIPAL_ID = "preferred_username";
    public static final String HARDCODED_TENANT_ID = "colossus";
    public static final String HARDCODED_PRINCIPAL_ID = "dummyUser";
    public static final String JWT_HEADER = "Authorization";
    private static final String HARDCODED_Key_ID = "dummyKeyID";
    private static final String JWT_HEADER_KID = "kid";
    private static final String HARDCODED_DUMMY_KEY_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCcgnvECUk27SaGCmVk3/LXValB2TaMiKHr5mCWoIu1pthKN58rs8yCjX2zP4NtY60FKN7RNjkf47ii2/ZvhGbIrmgxYQDl3ULAyYKnruds9q4azmSZq8cHYgDmIZb2yv5xylGZqoj0Le6q48ghF3cpD2rGu1x+GCiMglcSii3vUMqJ92cKLovFR361lROePxAIIJv1chlfw+aFReCFfe/LkZkvUfVLER/hsKbJnn+lN0zjyYvtqVKVxhNqPVKtYXSLFUu3T6eiyfRghuUIiREOVd89pUVcHgjrCG1RE+9ZHfQE3ySfB0MTiLMpBGHnpNcXffpEULqVF+l5q183mTafAgMBAAECggEARV13aR0Me/GDX2kAI0M2J8XpbCvPB14PFbRXHqstAGdf+RTJ0N6xRzNagLUnlLXWmzDQAD1d+Ao/bL68bBRsUYkq2Ke7dCUqTrOA2rykCSETbQIIiCsf3N/MsjI07FBvkRMwM0DnzgEFhGvL6Q6zW1lf45Bqn+ApKI0j9i2Tyjp87UF/A7cjeQH6BgOoNCMWpdnA9JheAj8JhSE/g7cwZOONr7kjNBylYhulKnn+DALzGOsUdoYWUnwr4sopbKQbJ8nEkkbn/OlonfkPt+RDzhxSw1VyajJaDAsSmbHgXFXAPKBkfjMrsKlLdBCcDaSaWJya0C1ZFECnKhpg2TatMQKBgQDNRuhFZd0JekG++b9aXbtXTv7CNw4svSLXG3+CVW1MzmkFoBIE746e9oYvOS/BUOUiIhuVfpwiXU0NaQXL5oGy53dW9rDxXgIbCtLTOJpUtkT+FrvnHe1QiZNVq0Bhp5lT5kyRPKbiWweVp/cqTtcZt1+KfHR5xpoYcqRV+Jf5hQKBgQDDLrswMPbuDAKRJSSD7ndSnjrEsZlUWS9ZYCBC0mNFpvG7qvhYvBu0xW9BPG3AcdH0XDK392ko4fQ8AY0oVz67zjI7pg90C3rbgiT5fvQLT4P+wEi7yy8CHKGB3NGvPHRrx+lhP00hPyHf2YooZmbVCcE6oLV61sLQhvhGFMq20wKBgEkiaxxWd1KqGQRmTFNws48emMsu/EDaPbBvnpUbxG4p1+bmKaVSh/iAkjv+3tG79XacukEfs5bWsN4HPcZwYGMR7kWwbEgCGzPxPj18nBd0PM5d4SItWmBoM3LBKZXhNVJb6YmRF0/r6J7cEcYkAqSp3UMkpgbAyCPPrFbo05RVAoGAaNkAmwmijAuNkXS3YdvG4222OhUBPctO4R7/FYY456CfeGWGVPxvkbMBabTRbDB4TWD9qc9RATIn5dgbgNElFPPWDi7NXFQSYjpOLGxjInRLfpzCNYcXMHY62CzLwuuhyogwORotD6//PWV5juw7TzOXRiwwcNVO/3+RfOwMXRsCgYEAulvdmeZrPK8JlrMCO9WDpLX2fkARaLIZmcI5BLOuUD16Y0W2OVjxPaScsESuIXDBgLPyhmE/EH0Zi5GTDrV+/78FsCcigcWpyTxZynPJI89arfUJhrzOvOW5uuJc6CcdmdGc6Z74a1K2ThCY690qbAwz53PnC1gywdTOCIuZ+F8=";

    private static String generatedToken;

    public static String createJWT(String tenantId) {
        if (StringUtils.isNotBlank(generatedToken)) {
            return generatedToken;
        }
        generatedToken = createJWT("tokenId", "Gemalto", "Subject", -1, getDummyJWTAuthenticationParams(), tenantId, HARDCODED_PRINCIPAL_ID);
        return generatedToken;
    }

    private static String createJWT(String id, String issuer, String subject, long ttlMillis, JwtAuthenticationParams params, String tenantId, String principalId) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Gson gson = new Gson();

        //We will sign our JWT with our private key. Public key is added to Jwt Security Service
        Key signingKey = getHardCodedPrivateKey(HARDCODED_DUMMY_KEY_PRIVATE_KEY);

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setHeaderParam(JWT_HEADER_KID, HARDCODED_Key_ID)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer);

        // Add resource access to jwt
        List<String> roles = params.getRoles();
        if (!roles.isEmpty()) {
            ResourceAccess resourceAccess = new ResourceAccess();
            ResourceAccess.FidoUAFComponent fidoUafServer = new ResourceAccess.FidoUAFComponent();

            fidoUafServer.setRoles(new Roles(roles));
            resourceAccess.setResourceAccess(fidoUafServer);

            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> resourceAccessMap = gson.fromJson(gson.toJson(resourceAccess), type);

            builder.setClaims(resourceAccessMap);
        }

        if (StringUtils.isNotBlank(principalId)) {
            builder.claim(JWT_ATTR_PRINCIPAL_ID, principalId);
        }

        if (StringUtils.isNotBlank(tenantId)) {
            builder.claim(JWT_ATTR_TENANT_ID, tenantId);
        }

        builder.signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return "Bearer " + builder.compact();
    }

    public static JwtAuthenticationParams getDummyJWTAuthenticationParams() {
        ConcurrentHashMap<String, String> publicKeyMap = new ConcurrentHashMap<String, String>();
        final JwtSecret secret = new JwtSecret(publicKeyMap);
        List<String> roles = createDummyRoles().getRoles();
        return new JwtAuthenticationParams(JWT_HEADER, secret, REQ_PARAM_TENANT_ID, roles);
    }

    public static Roles createDummyRoles() {
        Roles roles = new Roles();
        roles.add(ResourceAccess.Privileges.FIDO_UAF_SERVER_REGISTER.getRole());
        roles.add(ResourceAccess.Privileges.FIDO_UAF_SERVER_DEREGISTER.getRole());
        roles.add(ResourceAccess.Privileges.FIDO_UAF_SERVER_AUTHENTICATE.getRole());
        roles.add(ResourceAccess.Privileges.FIDO_UAF_SERVER_READ_APP_INFO.getRole());
        roles.add(ResourceAccess.Privileges.FIDO_UAF_SERVER_READ_TRUSTED_FACETS.getRole());

        return roles;
    }

    private static PrivateKey getHardCodedPrivateKey(String hardCodedDummyKeyPrivateKey) {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(hardCodedDummyKeyPrivateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey key = keyFactory.generatePrivate(keySpec);
            return key;
        } catch (Exception err) {
            //Log error
        }
        return null;
    }

}

class ResourceAccess {

    private FidoUAFComponent resource_access;

    public void setResourceAccess(FidoUAFComponent resourceAccess) {
        this.resource_access = resourceAccess;
    }

    public enum Privileges {
        FIDO_UAF_SERVER_REGISTER("fido_uaf_server-register"),
        FIDO_UAF_SERVER_AUTHENTICATE("fido_uaf_server-authenticate"),
        FIDO_UAF_SERVER_DEREGISTER("fido_uaf_server-deregister"),
        FIDO_UAF_SERVER_READ_APP_INFO("fido_uaf_server-read_app_info"),
        FIDO_UAF_SERVER_READ_TRUSTED_FACETS("fido_uaf_server-read_trusted_facet");

        private String role;

        private Privileges(final String role) {
            this.role = role;
        }

        public String getRole() {
            return this.role;
        }
    }

    ;

    public static class FidoUAFComponent {
        private Roles fido_uaf_server;

        public void setRoles(Roles roles) {
            this.fido_uaf_server = roles;
        }
    }

    public static class Roles {
        private List<String> roles;

        Roles() {
            this.roles = new ArrayList<String>();
        }

        Roles(List<String> roles) {
            this.roles = roles;
        }

        public void add(String role) {
            this.roles.add(role);
        }

        public boolean isEmpty() {
            return this.roles.isEmpty();
        }

        public List<String> getRoles() {
            return this.roles;
        }
    }
}


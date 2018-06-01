package org.gemalto.com.uaf;

import java.util.Map;

/**
 * Share JWT secret info
 */
public class JwtSecret {

    private final Map<String, String> publicKeyMap;

    public JwtSecret(Map<String, String> base64EncodedPublicKeyMap) {

        if (base64EncodedPublicKeyMap == null) {
            throw new NullPointerException("Key must be not null");
        }

        this.publicKeyMap = base64EncodedPublicKeyMap;
    }

    public String getBase64EncodedPublicKey(String keyId) {
        return publicKeyMap.get(keyId);
    }
}

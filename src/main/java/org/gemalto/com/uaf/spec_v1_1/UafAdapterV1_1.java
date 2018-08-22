package org.gemalto.com.uaf.spec_v1_1;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.gemalto.com.uaf.*;
import org.gemalto.com.uaf.spec_v1_0.GetUAFRequestV1_0;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created by drurenia on 2/13/2018.
 */
@Path("/v11")
public class UafAdapterV1_1 {

    private static final Gson gson = new Gson();
    private static final Logger logger = LogManager.getLogger(UafAdapterV1_1.class);

    @Context
    HttpServletRequest request;

    @GET
    @Path("/facets")
    @Produces("application/fido.trusted-apps+json")
    public String getTrustedFacets() {
        logger.debug("/facets");
        try {
            final String path = UafAdapterSettings.getTrustedFacetsPath();
            final String token = generateToken();
            logger.debug("Getting facet list from: " + path);
            return ClientBuilder.newBuilder()
                    .sslContext(AdapterUtils.createSSLContextForSelfSigned())
                    .hostnameVerifier(AdapterUtils.createHostnameVerifierByPass())
                    .build()
                    .target(path)
                    .request("application/fido.trusted-apps+json")
                    .header("Authorization", token)
                    .get(String.class);
        } catch (Exception exp) {
            logger.error(exp);
            throw exp;
        }
    }

    @GET
    @Path("/alive")
    @Produces(MediaType.TEXT_PLAIN)
    public String localAlive() {
        logger.debug("/alive");
        return "OK";
    }

    @POST
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public String regAndAuthRequest(String payload) {
        final Clock clock = new Clock().start();
        if (StringUtils.isEmpty(payload) || StringUtils.isBlank(payload)) {
            return payload;
        }

        try {
            logger.debug("Received X Request: " + payload);

            final GetUAFRequest req = parseRequest(payload);
            final String url = req.resolveUrl(AdapterUtils.resolveAppId(request));

            logger.debug("Operation is: " + req.getOperation());

            Invocation.Builder invocationBuilder = createInvokationBuilder(url);

            logger.debug("Request To: " + url);
            javax.ws.rs.core.Response response = (req.getOperation() == Operation.Dereg) ?
                    invocationBuilder.delete(javax.ws.rs.core.Response.class) :
                    invocationBuilder.get(javax.ws.rs.core.Response.class);

            final ReturnUAFRequest returnUAFRequest = ReturnUAFRequest.fromResponse(req, response);
            final String output = gson.toJson(returnUAFRequest);

            logger.debug(String.format("Returning %s Request in %d ms: %s", req.getOperation().name(), clock.stopAndGetTotal(), output));
            return output;
        } catch (Exception ex) {
            logger.error(ex);
            throw ex;
        }
    }

    @POST
    @Path("/respond")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String regAndAuthResponse(String payload) throws IOException {

        try {
            return resolveResponse(payload)
                    .processResponse();
        } catch (Exception e) {
            logger.error(e);

            UafResponse response = new UafResponse();
            response.setUafResponse("");
            response.setStatusCode(UafStatusCode.UnacceptableContent.getCode());
            return gson.toJson(response);
        }
    }

    private Response resolveResponse(final String payload) {
        if (payload.contains(Operation.Reg.name())) {
            return new RegistrationResponse(payload);
        }
        return new AuthenticationResponse(payload);
    }

    private Invocation.Builder createInvokationBuilder(final String url) {
        Client client = ClientBuilder.newBuilder()
                .sslContext(AdapterUtils.createSSLContextForSelfSigned())
                .hostnameVerifier(AdapterUtils.createHostnameVerifierByPass())
                .build();
        WebTarget webTarget = client.target(url);
        return webTarget.request("application/json")
                .header("Authorization", generateToken());
    }

    private String generateToken() {
        return JWTCreator.createJWT(JWTCreator.HARDCODED_TENANT_ID);
    }

    private GetUAFRequest parseRequest(String payload) {
        if (payload.contains("userName")) {
            return gson.fromJson(payload, GetUAFRequestV1_0.class);
        }
        return gson.fromJson(payload, GetUAFRequestV1_1.class);
    }

    @FunctionalInterface
    private interface Response {
        String processResponse() throws IOException;
    }

    private class RegistrationResponse implements Response {

        private final String payload;

        public RegistrationResponse(final String payload) {
            logger.debug("Received Registration Response: " + payload);
            this.payload = payload;
        }

        @Override
        public String processResponse() throws IOException {
            final Clock clock = new Clock().start();

            final String registrationResponse = AdapterUtils.convertStringToJsonFiled(payload);

            logger.debug("Passing upstream: " + registrationResponse);

            final String registrationRecord = AdapterUtils.passRequest(registrationResponse, UafAdapterSettings.getRegistrationResponsePath());

            RegistrationRecord[] resp = gson.fromJson(registrationRecord, RegistrationRecord[].class);

            UafStatusCode code = UafStatusCode.fromStatus(resp[0].getStatus());

            UafResponse uafResponse = new UafResponse();
            uafResponse.setUafResponse(registrationRecord);
            uafResponse.setStatusCode(code.getCode());

            final String output = gson.toJson(uafResponse);
            logger.debug(String.format("Returning Registration Response in %d ms: %s", clock.stopAndGetTotal(), output));
            return output;
        }
    }

    private class AuthenticationResponse implements Response {

        private final String payload;

        public AuthenticationResponse(String payload) {
            logger.debug("Received Authentication Response: " + payload);
            this.payload = payload;
        }

        @Override
        public String processResponse() throws IOException {
            final Clock clock = new Clock().start();
            final String authenticationResponse = AdapterUtils.convertStringToJsonFiled(payload);

            logger.debug("Passing upstream: " + authenticationResponse);

            final String authenticationRecord = AdapterUtils.passRequest(authenticationResponse, UafAdapterSettings.getAuthenticationResponsePath());

            AuthenticatorRecord[] resp = gson.fromJson(authenticationRecord, AuthenticatorRecord[].class);

            UafStatusCode code = UafStatusCode.fromStatus(resp[0].getStatus());

            UafResponse uafResponse = new UafResponse();
            uafResponse.setUafResponse(authenticationRecord);
            uafResponse.setStatusCode(code.getCode());

            final String output = gson.toJson(uafResponse);
            logger.debug(String.format("Returning Registration Response in %d ms: %s", clock.stopAndGetTotal(), output));
            return output;
        }
    }


}

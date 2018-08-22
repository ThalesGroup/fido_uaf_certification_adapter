package org.gemalto.com.uaf.spec_v1_0;


import com.google.gson.Gson;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.gemalto.com.uaf.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Path("/v10")
public class UafAdapterV1_0 {

    //Static counters for logging purposes
    static int get = 0;
    static int sendReg = 0;
    static int sendAuth = 0;
    private final Logger logger = LogManager.getLogger(UafAdapterV1_0.class);

    private final Gson gson = new Gson();

    @Context
    HttpServletRequest request;
    @Context
    HttpServletResponse response;
    @Context
    ServletContext context;

    @GET
    @Path("/LocalAlive")
    @Produces(MediaType.TEXT_PLAIN)
    public String localAlive() {


        logger.debug("/LocalAlive");

        get = 0;
        sendReg = 0;
        sendAuth = 0;
        return "OK";
    }

    @GET
    @Path("/UpstreamUafAlive")
    @Produces(MediaType.TEXT_PLAIN)
    public String upstreamUafAlive() throws IOException {

        logger.debug("/UpstreamUafAlive");
        String target = UafAdapterSettings.getUafInfoPath();
        logger.debug("Passing request to Uaf Server: " + target);
        URL upsteamUaf = new URL(target);

        HttpURLConnection conn = (HttpURLConnection) upsteamUaf.openConnection();

        try {

            int respCode = conn.getResponseCode();

            if (respCode != 200) {
                response.setStatus(respCode);
                return "Upstream server responded with:" + respCode;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            StringBuffer stringBuffer = new StringBuffer();

            String output;

            while ((output = br.readLine()) != null) {
                stringBuffer.append(output);
            }

            return stringBuffer.toString();

        } finally {
            conn.disconnect();

        }


    }

    @POST
    @Path("/Get")
    @Produces(MediaType.APPLICATION_JSON)
    public String GetUAFRequest(String payload) {
        return getUAFRequestInternal(payload);
    }

    @POST
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public String getUAFRequest(String payload) {
        return getUAFRequestInternal(payload);
    }

    private String getUAFRequestInternal(String payload){
        try {
            AdapterUtils.logAll(request);
            logger.debug("UAF UafAdapter /Get Request: " + get++);
            logger.debug("Received: " + payload);

            String output = "";

            if (!payload.isEmpty()) {

                GetUAFRequest req = parseRequest(payload);

                String url = req.resolveUrl(AdapterUtils.resolveAppId(request));

                logger.debug("Operation is: " + req.getOperation());

                Invocation.Builder invocationBuilder = createInvokationBuilder(url);

                logger.debug("Request To: " + url);

                Response response = null;
                switch (req.getOperation()) {
                    case Reg:
                    case Auth:
                        response = invocationBuilder.get(Response.class);
                        break;
                    case Dereg:
                        response = invocationBuilder.delete(Response.class);
                        break;
                }
                UAFRequest outputRequest = UAFRequest.fromResponse(req, response);
                output = gson.toJson(outputRequest);

            }

            logger.debug("Returning: " + output);
            return output;
        } catch (Exception ex) {
            logger.error(ex);
            throw ex;
        }
    }

    private GetUAFRequest parseRequest(String payload) {
        return gson.fromJson(payload, GetUAFRequestV1_0.class);

    }

    //RESPONSES

    @POST
    @Path("/Send/Reg")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String UAFRegResponse(String payload) throws ServletException, IOException {
        AdapterUtils.logAll(request);
        logger.debug("UAF UafAdapter /Send/Reg Request: " + sendReg++);
        logger.debug("Received: " + payload);

        payload = AdapterUtils.convertStringToJsonFiled(payload);

        logger.debug("Passing upstream: " + payload);

        try {

            String out = AdapterUtils.passRequest(payload, UafAdapterSettings.getRegistrationResponsePath());

            RegistrationRecord[] resp = gson.fromJson(out, RegistrationRecord[].class);

            UafStatusCode code = UafStatusCode.fromStatus(resp[0].getStatus());

            UafResponse response = new UafResponse();
            response.setUafResponse(out);
            response.setStatusCode(code.getCode());


            String output = gson.toJson(response);

            logger.debug("Returning: " + output);
            return output;
        } catch (Exception e) {
            logger.error(e);

            UafResponse response = new UafResponse();
            response.setUafResponse("");
            response.setStatusCode(UafStatusCode.UnacceptableContent.getCode());
            return gson.toJson(response);
        }

    }

    @POST
    @Path("/Send/Auth")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String UAFAuthResponse(String payload) throws ServletException, IOException {
        logger.debug("UAF UafAdapter /Send/Auth Request: " + sendAuth++);
        logger.debug("Received: " + payload);
        payload = AdapterUtils.convertStringToJsonFiled(payload);
        logger.debug("Passing upstream: " + payload);
        try {

            String out = AdapterUtils.passRequest(payload, UafAdapterSettings.getAuthenticationResponsePath());


            AuthenticatorRecord[] resp = gson.fromJson(out, AuthenticatorRecord[].class);

            UafStatusCode code = UafStatusCode.fromStatus(resp[0].getStatus());

            UafResponse response = new UafResponse();
            response.setUafResponse(out);
            response.setStatusCode(code.getCode());


            String output = gson.toJson(response);

            logger.debug("Returning: " + output);
            return output;

        } catch (Exception e) {
            logger.error(e);
            UafResponse response = new UafResponse();
            response.setUafResponse("");
            response.setStatusCode(UafStatusCode.UnacceptableContent.getCode());
            return gson.toJson(response);
        }

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

}

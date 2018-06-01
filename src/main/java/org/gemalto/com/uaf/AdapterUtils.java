package org.gemalto.com.uaf;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

public class AdapterUtils {

    private static final Logger logger = LogManager.getLogger(AdapterUtils.class);

    public static void logAll(HttpServletRequest request) {


        logger.debug(request.getRemoteAddr());

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logger.trace("Header Name - " + headerName + ", Value - " + request.getHeader(headerName));

        }
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            logger.trace("Parameter Name - " + paramName + ", Value - " + request.getParameter(paramName));
        }
    }


    public static String passRequest(String payload, String target) throws IOException {

        logger.debug("Passing request to Uaf Server: " + target);

        URL url = new URL(target);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try {

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", JWTCreator.createJWT(JWTCreator.HARDCODED_TENANT_ID));
            conn.getOutputStream().write(payload.getBytes());
            conn.getOutputStream().flush();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                logger.debug("Server returned with HTTP " + responseCode);

                //   return "{\"statusCode\":1500,\"Description\":\"ERROR. Operation completed\",\"token\":null,\"location\":null,\"postData\":null,\"newUAFRequest\":null}";
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

    /**
     * Converts the indicated field in the passed json into a escaped string and returns the full json with
     * the indicated field replaced as a string.
     * Example:
     * {"context": {"userName": "fidoUser1"}, "op": "Reg"}
     * Produces:
     * {"context": "{\"userName\": \"fidoUser1\"}", "op": "Reg"}
     * Provided that "context" is passed as fieldName.
     *
     * @param json      The json structure to modify
     * @param fieldName The field to escape and return as a string.
     * @return the provided json with a escaped filed
     * @throws IOException
     */
    static String convertFieldToString(String json, String fieldName) throws IOException {

        JsonFactory factory = new JsonFactory();

        ObjectMapper mapper = new ObjectMapper(factory);
        mapper.disableDefaultTyping();
        JsonNode rootNode = mapper.readTree(json);

        ObjectNode jNode = mapper.createObjectNode();

        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> f = fieldsIterator.next();
            if (f.getKey().equalsIgnoreCase(fieldName)) {
                jNode.put(f.getKey(), f.getValue().toString());
            } else {
                jNode.set(f.getKey(), f.getValue());
            }

        }

        return jNode.toString();
    }

    public static String convertStringToJsonFiled(String json) throws IOException {
        JsonFactory factory = new JsonFactory();

        ObjectMapper mapper = new ObjectMapper(factory);
        mapper.disableDefaultTyping();
        ObjectNode jNode = mapper.createObjectNode();


        JsonNode rootNode = mapper.readTree(json);

        String str = rootNode.get("uafResponse").textValue();


        JsonNode uafResponseValue = mapper.readTree(str);
        jNode.set("uafResponse", uafResponseValue);
        return uafResponseValue.toString();

    }

    public static String resolveAppId(final HttpServletRequest request) {
        final String hostname = request.getServerName();
        final String contextPath = request.getContextPath();

        final String appId = String.format("https://%s%s%s", hostname, contextPath, UafAdapterSettings.getFacetsEndpoint());
        return new String(Base64.getUrlEncoder()
                .withoutPadding()
                .encode(appId.getBytes(Charset.defaultCharset())),
                Charset.defaultCharset());
    }

}

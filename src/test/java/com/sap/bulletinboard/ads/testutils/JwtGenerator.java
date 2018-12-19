package com.sap.bulletinboard.ads.testutils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Create tokens with a fixed private/public key and dummy values.
 * The client ID, identity zone, and scopes are configurable.
 */
public class JwtGenerator {

    private static final String PRIVATE_KEY_FILE = "/privateKey.txt";
    private static final String PUBLIC_KEY_FILE = "/publicKey.txt";
    private final String clientId;
    private final String identityZone;
    private static final String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:saml2-bearer";
    private String[] scopes;
    private ArrayList<Attribute> attributes = new ArrayList<>();
    private String userName = "testuser";

    /**
     * @param clientId     the client ID that will be used for any created token
     * @param identityZone the identity zone that will be used for any created token
     */
    public JwtGenerator(String clientId, String identityZone) {
        this.clientId = clientId;
        this.identityZone = identityZone;
    }

    public JwtGenerator() {
        this.clientId = "testClient!t27";
        this.identityZone = "a09a3440-1da8-4082-a89c-3cce186a9b6c";
    }

    public JwtGenerator setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    /**
     * @param scopes the scopes that should be part of the token
     * @return the JwtGenerator itself
     */
    public JwtGenerator addScopes(String... scopes) {
        this.scopes = scopes;
        return this;
    }

    /**
     * @param attribute       the attribute name that should be part of the token
     * @param attributeValues the attribute value that should be part of the token
     * @return the JwtGenerator itself
     */
    public JwtGenerator addAttribute(String attribute, String[] attributeValues) {
        attributes.add(new Attribute(attribute, attributeValues));
        return this;
    }

    public String getTokenForAuthorizationHeader() {
        return "Bearer " + getToken();
    }

    /**
     * @return the JWT with the given scopes
     */
    public String getToken() {
        ObjectNode root = getBasicJsonObject();
        if (scopes != null && scopes.length > 0) {
            root.set("scope", getScopesJSON(scopes));
        }
        if (attributes.size() > 0) {
            root.set("xs.user.attributes", getAttributesJSON(attributes.toArray(new Attribute[attributes.size()])));
        }

        return getTokenForClaims(root.toString());
    }

    /**
     * @return the basic token in JSON
     */
    private ObjectNode getBasicJsonObject() {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode root = mapper.createObjectNode();
        root.put("client_id", getClientId());
        root.put("cid", getClientId());
        root.put("exp", Integer.MAX_VALUE);
        root.put("user_name", userName);
        root.put("user_id", "D012345");
        root.put("email", userName + "@test.org");
        root.put("zid", getIdentityZone());
        root.put("grant_type", GRANT_TYPE);

        return root;
    }

    private static JsonNode getScopesJSON(String[] scopes) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode scopesArray = mapper.createArrayNode();
        for (String scope : scopes) {
            scopesArray.add(scope);
        }
        return scopesArray;
    }

    // convert Java array into JSON array
    private static JsonNode getAttributesJSON(Attribute... attributes) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode attributesNode = mapper.createObjectNode();
        for (Attribute attribute : attributes) {
            ArrayNode attributeValueArray = mapper.createArrayNode();
            for (String attributeValue : attribute.getValues()) {
                attributeValueArray.add(attributeValue);
            }
            attributesNode.set(attribute.getName(), attributeValueArray);
        }
        return attributesNode;
    }

    private static String getTokenForClaims(String claims) {
        RsaSigner signer = new RsaSigner(readFromFile(PRIVATE_KEY_FILE));
        return JwtHelper.encode(claims, signer).getEncoded();
    }

    public String getClientId() {
        return clientId;
    }

    public String getIdentityZone() {
        return identityZone;
    }

    /**
     * @return the public key used to sign the tokens
     */
    public static String getPublicKey() {
        String publicKey = readFromFile(PUBLIC_KEY_FILE);
        return removeLinebreaks(publicKey);
    }

    private static String removeLinebreaks(String input) {
        return input.replace("\n", "").replace("\r", "");
    }

    private static String readFromFile(String path) {
        InputStream inputStream = null;
        try {
            inputStream = JwtGenerator.class.getResourceAsStream(path);
            return IOUtils.toString(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static class Attribute {
        private String[] values;
        private String name;

        public Attribute(String name, String[] values) {
            this.name = name;
            this.values = values;
        }

        public String getName() {
            return name;
        }

        public String[] getValues() {
            return values;
        }
    }
}

package uk.co.bbc.opensocial.peggy.input;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import bbc.forge.config.PropertiesMuncher;
import bbc.forge.urlfetcher.Apache4UrlFetcher;
import bbc.forge.urlfetcher.Request;
import bbc.forge.urlfetcher.Response;
import bbc.forge.urlfetcher.UrlFetcher;

public class JSONClient {
    private static Logger logger = Logger.getLogger("JSONClient logger");
    
    protected final UrlFetcher urlFetcher;

    public final static String KEYSTORE_LOCATION = getSystemProperty(
            "javax.net.ssl.keyStore", "/etc/pki/SocialDev.p12");

    public final static String KEYSTORE_PWD = getSystemProperty(
            "javax.net.ssl.keyStorePassword", "SocialDev");

    public final static String TRUST_STORE = getSystemProperty(
            "javax.net.ssl.trustStore", "/etc/pki/jssecacerts");

    public JSONClient() {
        this.urlFetcher = defaultUrlFetcher();
    }
    
    /**
     * Default url fetcher.
     * 
     * @return the url fetcher
     */
    protected UrlFetcher defaultUrlFetcher() {
        Apache4UrlFetcher urlFetcher = new Apache4UrlFetcher();
        urlFetcher.createClient();
        return urlFetcher;
    }

    /**
     * Gets the system property.
     * 
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * 
     * @return the system property
     */
    protected static String getSystemProperty(String key, String defaultValue) {
        String systemProperty = System.getProperty(key);
        if (null == systemProperty) {
            systemProperty = PropertiesMuncher.munch().getProperty(key);
            if (null == systemProperty) {
                systemProperty = defaultValue;
            }
        }
        System.setProperty(key, systemProperty);
        return systemProperty;
    }

    /**
     * Create a JSONObject based on the content returned from a url.
     * 
     * @param url
     * @return
     */
    public JSONObject getJSONObjectFromUrl(String url) {
        try {
            Response response = urlFetcher.get(buildRequest(url));
            return new JSONObject(response.getContent());
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * build the request.
     * @param url
     * @return
     */
    protected static Request buildRequest(String url) {
        Request request = new Request();
        request.setFollowRedirects(true);
        request.addRequestHeader("Accept", "application/json");
        request.setAllowTruncated(false);
        request.setTimeout(20000);
        try {
            request.setUri(new URI(url));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return request;
    }
}

package uk.co.bbc.opensocial.peggy.input;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.bbc.opensocial.peggy.EventSwitch;
import uk.co.bbc.opensocial.peggy.event.ServiceStatus;

public class ServiceStatusReporter implements Reporter{
    Logger logger = Logger.getLogger(this.getClass());
    
    private final String url;
    private final String serviceName;
    private EventSwitch eventSwitch = EventSwitch.getInstance();
    private final JSONClient jsonClient;
    
    public final static String STATUS_ARRAY = "servicestatus";
    public final static String STATUS_KEY = "status";
    
    public ServiceStatusReporter(String serviceName, String url) {
        this.url = url;
        this.serviceName = serviceName;
        jsonClient = new JSONClient();
    }


    /**
     * get latest 
     */
    public void update() {
        JSONObject jsonObject = jsonClient.getJSONObjectFromUrl(url);
        if (jsonObject == null) {
            return;
        }
        try {
            JSONArray statuses = jsonObject.getJSONArray(STATUS_ARRAY);
            for (int i = 0, limit = statuses.length(); i < limit; i++) {
                JSONObject status = statuses.getJSONObject(i);
                Iterator<String> it = status.keys();
                String statusName = null;
                while(it.hasNext()) {
                    statusName = it.next();
                }
                JSONObject statusDetails = status.getJSONObject(statusName);
                boolean success = statusDetails.getBoolean(STATUS_KEY);
                eventSwitch.reportEvent(new ServiceStatus(serviceName, statusName, success));
            }
            
        } catch (JSONException e) {
            logger.info(e.getStackTrace());
        }
    }
    
    public boolean configure() {
        // TODO Auto-generated method stub
        return true;
    }

    public void disable() {
        // TODO Auto-generated method stub
        
    }

    public void enable() {
        // TODO Auto-generated method stub
        
    }


    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }
}

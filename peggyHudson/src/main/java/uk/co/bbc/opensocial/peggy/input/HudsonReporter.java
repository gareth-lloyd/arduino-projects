package uk.co.bbc.opensocial.peggy.input;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.bbc.opensocial.peggy.EventSwitch;
import uk.co.bbc.opensocial.peggy.event.HealthReport;

public class HudsonReporter implements Reporter{
    Logger logger = Logger.getLogger(this.getClass());
    private final String urlToHit;
    private EventSwitch eventSwitch = EventSwitch.getInstance();
    private final JSONClient jsonClient;
    
    public final static String DISPLAY_NAME_KEY = "fullDisplayName";
    
    public final static String ACTIONS_KEY = "actions";
    public final static String FAIL_COUNT_KEY = "failCount";
    public final static String SKIP_COUNT_KEY = "skipCount";
    public final static String TOTAL_COUNT_KEY = "totalCount";
    
    public final static String HUDSON_API_SUFFIX = "/lastBuild/api/json"; 
    
    
    
    public HudsonReporter(String hudsonInstance, String projectName) {
        urlToHit = hudsonInstance + projectName + HUDSON_API_SUFFIX;
        jsonClient = new JSONClient();
    }


    /**
     * get latest 
     */
    public void update() {
        JSONObject jsonObject = jsonClient.getJSONObjectFromUrl(urlToHit);
        try {
            JSONArray actionsJsonArray = jsonObject.getJSONArray(ACTIONS_KEY);
            
            JSONObject testResults = null;
            for (int i = 0, limit = actionsJsonArray.length(); i < limit; i++) {
                testResults = actionsJsonArray.getJSONObject(i);
                System.out.println(testResults.toString(2));
                if (testResults.has(FAIL_COUNT_KEY)) {
                    break;
                }
            }
            int totalTests = Integer.parseInt(testResults.getString(TOTAL_COUNT_KEY));
            int failedTests = Integer.parseInt(testResults.getString(FAIL_COUNT_KEY));
            
            String title = jsonObject.getString(DISPLAY_NAME_KEY);
            eventSwitch.reportEvent(new HealthReport(title, totalTests, failedTests));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        logger.info("finished getting json");
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
}

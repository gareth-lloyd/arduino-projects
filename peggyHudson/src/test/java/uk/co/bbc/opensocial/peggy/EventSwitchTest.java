package uk.co.bbc.opensocial.peggy;


import org.junit.Before;
import org.junit.Test;


public class EventSwitchTest {

    EventSwitch eventSwitch;
    
    @Before
    public void setup() {
        eventSwitch = EventSwitch.getInstance();
    }

    @Test
    public final void testGetInstance() {
    }
    
    @Test
    public final void testRegisterHandler() {
        TestDisplay td1 = new TestDisplay();
        TestDisplay td2 = new TestDisplay();
        
        eventSwitch.registerConsumer(td1);
        eventSwitch.registerConsumer(td2);
        
        eventSwitch.printConsumers();
    }
    
    @Test
    public final void testReportEvent() {
        eventSwitch.reportEvent(new Object());
        
    }
    
    
    @Test
    public void testSomething() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

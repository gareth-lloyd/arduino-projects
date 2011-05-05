package uk.co.bbc.opensocial.peggy;


public class TestDisplay {
    
    public void produceOutput(Object o) {
        System.out.println("output from an Object");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
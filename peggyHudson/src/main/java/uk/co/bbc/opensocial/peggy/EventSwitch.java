package uk.co.bbc.opensocial.peggy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * The EventSwitch accepts objects representing events of certain kinds,
 * places them on a queue, and hands them off to consumers who have 
 * registered to receive objects of a certain type. 
 * 
 * It is a publish-subscribe system.
 * 
 * We go through the trouble of registering consumers by the type of 
 * object consumed so that consumers can be as simple as possible: they 
 * do not have to manage their own threads or test the type of
 * incoming objects, but instead can merely register and wait for their
 * consuming methods to be called.
 * 
 * This comes at a certain cost, however...
 * 
 * @author glloyd
 *
 */
public final class EventSwitch {
    public static final String HANDLER_METHOD_NAME = "produceOutput";
    
    private static EventSwitch theOneInstance = null;

    private BlockingQueue<Object> queue = new LinkedBlockingQueue<Object>(100);

    private ConcurrentMap<Class<?>, Map<Object, Method>> classConsumerRegister = 
        new ConcurrentHashMap<Class<?>, Map<Object, Method>>();

    private final Thread consumerThread = new Thread(new ConsumingThread());
    
    private EventSwitch() {
        consumerThread.start();
    }

    /*
     * This Runnable should be the only one able to consume from the Blocking
     * queue.
     * 
     * In this architecture, we have one worker thread performing all work.
     * It is a simple and fault-prone architecture in which bad code in a 
     * registered consumer object could slow or crash the whole app.
     * 
     * At some point, this should be replaced by some sort of thread pool.
     */
    private class ConsumingThread implements Runnable {
        /*
         * repeatedly call consume method
         */
        public void run() {
            while (true) {
                try {
                    consume();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        /*
         * Take an event from the queue and send it to any registered consumers.
         */
        private void consume() throws InterruptedException {
            // this call blocks until an Object is available.
            Object event = queue.take();

            Class<?> eventClass = event.getClass();
            Map<Object, Method> consumers = classConsumerRegister
                    .get(eventClass);

            if (consumers == null) {
                // Nothing has registered to consume this object.
                return;
            }

            for (Entry<Object, Method> entry: consumers.entrySet()) {
                Method m = entry.getValue();
                try {
                    m.invoke(entry.getKey(), eventClass.cast(event));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Illegal Argument", e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Illegal Access", e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Invocation Target Exception", e);
                }
            }
        }
    }

    /**
     * Factory method enforcing singleton pattern
     * 
     * @return
     */
    public static synchronized EventSwitch getInstance() {
        if (theOneInstance == null) {
            theOneInstance = new EventSwitch();
        }
        return theOneInstance;
    }

    /**
     * Report an event for handling.
     * 
     * @param event
     */
    public void reportEvent(Object event) {
        if (event == null)
            return;
        
        try {
            queue.put(event);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reflect on the passed consumer object to find any public methods with the
     * required name.
     * 
     * For each consumer method found, Create a map entry keyed by the consumer
     * object so that we register a combination of object and method that can be
     * used to consume objects later.
     * 
     * 
     * @param consumer
     */
    public void registerConsumer(Object consumer) {
        Method[] methods = consumer.getClass().getMethods();
        for (Method method : methods) {
            if (HANDLER_METHOD_NAME.equals(method.getName())) {
                registerConsumerMethod(consumer, method);
            }
        }
    }

    /**
     * Register a method as a consumer.
     * 
     * @param consumer
     *            The object
     * @param method
     */
    public void registerConsumerMethod(Object consumer, Method method) {
        // Find the class that this method can handle:
        Class<?>[] parameterClasses = method.getParameterTypes();
        if (parameterClasses.length != 1) {
            throw new IllegalArgumentException(
                    "The method registered must take one argument exactly.");
        }
        Class<?> classToConsume = parameterClasses[0];

        // Insert a new Map if not present, in a thread safe way:
        classConsumerRegister.putIfAbsent(classToConsume,
                new HashMap<Object, Method>());
        Map<Object, Method> classConsumers = classConsumerRegister
                .get(classToConsume);

        // register the consumer and its consuming method
        classConsumers.put(consumer, method);
    }

    /**
     * output a formatted consumers list.
     */
    public void printConsumers() {
        for (Class<?> c : classConsumerRegister.keySet()) {
            System.out.println("Consumers of " + c.getName() + ": ");

            Map<Object, Method> handlers = classConsumerRegister.get(c);
            for (Object o : handlers.keySet()) {
                System.out.println("    " + o.getClass().getName());
                System.out.println("         " + handlers.get(o).getName());
            }
        }
    }
}

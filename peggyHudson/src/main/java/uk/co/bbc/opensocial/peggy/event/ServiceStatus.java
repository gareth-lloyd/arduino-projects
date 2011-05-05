package uk.co.bbc.opensocial.peggy.event;


public class ServiceStatus implements Comparable<ServiceStatus> {
    private String serviceName;
    private String statusName;
    private boolean working;
    
    public ServiceStatus(String serviceName, String statusName, boolean working) {
        this.serviceName = serviceName;
        this.statusName = statusName;
        this.working = working;
    }

    /**
     * @return the success
     */
    public boolean isWorking() {
        return working;
    }
    
    /**
     * @return the statusName
     */
    public String getStatusName() {
        return statusName;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }
    
    /**
     * Logical equality is defined to be a service status
     * for the same service name, and the same status name. 
     * 
     * whether or not the 'working' property is true is 
     * irrelevant to the logical equality because this 
     * may change over time for the same service status.  
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof ServiceStatus)) {
            return false;
        }
        ServiceStatus comparedStatus = (ServiceStatus) o;
        if (comparedStatus.getServiceName().equals(serviceName) &&
            comparedStatus.getStatusName().equals(statusName)) {
            return true;
        }
        else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return serviceName.hashCode() * statusName.hashCode();
    }

    /**
     * Compares first on service name, and if this is the same, compares
     * on status name as well. 
     * 
     * @param ServiceSTatus to compare
     * @return negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than
     */
    public int compareTo(ServiceStatus comparedStatus) {
        int serviceNameComparison = this.serviceName.compareTo(comparedStatus.getServiceName());
        if (serviceNameComparison != 0) {
            return serviceNameComparison;
        }
        else {
            return this.statusName.compareTo(comparedStatus.getStatusName());
        }
    }
}

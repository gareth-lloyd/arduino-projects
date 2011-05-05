package uk.co.bbc.opensocial.peggy.event;

public class HealthReport {

    private String projectName;
        
    private int score;
    
    public HealthReport(String projectName, int total, int failed) {
        this.projectName = projectName;
        this.score = (int) ((1.0 - ((double) failed / (double) total)) * 100.0);
    }

    
    /**
     * @return the description
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param description the description to set
     */
    public void setProjectName(String description) {
        this.projectName = description;
    }

    /**
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(int score) {
        this.score = score;
    }
    
}

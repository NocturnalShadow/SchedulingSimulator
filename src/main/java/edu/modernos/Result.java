package edu.modernos;

public class Result {
    public String schedulingType;
    public String schedulingName;
    public int totalRunTime = 0;

    public Result (String schedulingType, String schedulingName) {
        this.schedulingType = schedulingType;
        this.schedulingName = schedulingName;
    }
}

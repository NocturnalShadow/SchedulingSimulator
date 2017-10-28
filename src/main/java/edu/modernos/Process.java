package edu.modernos;

public class Process {
    private static int lastId = 0;

    public String id            = "Def";
    public int cpuTimeNeeded    = 0;
    public int ioBlockInterval  = 0;
    public int cpuTimeUsed      = 0;
    public int timesBlocked     = 0;

    public Process() { };
    public Process(int cpuTimeNeeded, int ioBlockInterval) {
        this.id                 = String.valueOf(lastId++);
        this.cpuTimeNeeded      = cpuTimeNeeded;
        this.ioBlockInterval    = ioBlockInterval;
    }
}

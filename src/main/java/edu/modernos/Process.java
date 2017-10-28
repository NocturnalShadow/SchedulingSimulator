package edu.modernos;

public class Process {
    private static int lastId = 0;

    public int id            	= 0;
    public int priority         = 1;
    public int quantum          = 0;
    public int cpuTimeNeeded    = 0;
    public int ioBlockInterval  = 0;
    public int ioNextBlock      = 0;
    public int cpuTimeUsed      = 0;
    public int timesBlocked     = 0;

    public Process() { };
    public Process(int cpuTimeNeeded, int ioBlockInterval) {
        this.id                 = lastId++;
        this.cpuTimeNeeded      = cpuTimeNeeded;
        this.ioBlockInterval    = ioBlockInterval;
    }
}

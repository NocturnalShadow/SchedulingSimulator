// Run() method is called from Simulator.main() and is where
// a scheduling algorithm written by the user resides.
// User modification should occur within the Run() method.

package edu.modernos;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler {
    private static void Log(Process process, String event, PrintStream out) {
        out.format("%-3s %-14s %-6s %6s ms | %-6s%n",
                process.id, event, process.ioBlockInterval + " ms", process.cpuTimeUsed, process.cpuTimeNeeded + " ms");
    }

    public static Result Run(LinkedList<Process> processes, int maxRunTime, String logFileName) {
        Queue<Process> processQueue = new LinkedBlockingQueue<Process>(processes);
        Process defaultProcess = new Process();
        processQueue.add(defaultProcess);

        Result result = new Result(
                "Batch (Non-preemptive)",
                "First-Come First-Served");

        try (PrintStream out = new PrintStream(new FileOutputStream(logFileName))) {
            out.format("%-3s %-14s %-10s %10s%n",
                    "#", "State", "I/O Block", "Progress");

            for(Process process : processQueue) {
                if(process == defaultProcess) {
                    processQueue.add(processQueue.remove());
                    continue;
                }

                Log(process, "registered", out);
                if(process.ioBlockInterval < process.cpuTimeNeeded - process.cpuTimeUsed && process.ioBlockInterval != 0) {
                    result.totalRunTime += process.ioBlockInterval;
                    process.cpuTimeUsed += process.ioBlockInterval;
                    process.timesBlocked++;
                    processQueue.add(processQueue.remove());
                    Log(process, "I/O blocked", out);
                } else {
                    result.totalRunTime += process.cpuTimeNeeded - process.cpuTimeUsed;
                    process.cpuTimeUsed = process.cpuTimeNeeded;
                    processQueue.remove();
                    Log(process, "completed", out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return result;
    }
}

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
        out.format("%-3s %-13s %-7s %-7s %-6s %6s ms | %-6s%n",
                process.id, event, process.priority,
                process.quantum + " ms",
                process.ioBlockInterval + " ms",
                process.cpuTimeUsed,
                process.cpuTimeNeeded + " ms");
    }

    public static Result Run(LinkedList<Process> processes, int maxRunTime, String logFileName) {
        Queue<Process> processQueue = new LinkedBlockingQueue<Process>();
        Queue<Process> pendingProcessQueue = new LinkedBlockingQueue<>(processes);

        Process guard = new Process();
        processQueue.add(guard);

        Result result = new Result(
                "Batch (Non-preemptive)",
                "First-Come First-Served");

        try (PrintStream out = new PrintStream(new FileOutputStream(logFileName))) {
            out.format("%-3s %-10s %-9s %-8s %-10s %10s %n",
                    "#", "State", "Priority", "Quantum", "I/O Block", "Progress");


            int quantum = 1;
            while(true) {
                for(Process process : pendingProcessQueue) {
                    processQueue.add(process);
                    pendingProcessQueue.remove();
                    process.quantum = quantum;
                }

                for(Process process : processQueue) {
                    if(process == guard) {
                        processQueue.add(processQueue.remove());
                        continue;
                    }

                    Log(process, "registered", out);

                    while(true) {
                        result.totalRunTime++;

                        process.quantum--;
                        process.ioNextBlock++;
                        process.cpuTimeUsed++;

                        if(process.cpuTimeNeeded == process.cpuTimeUsed) {
                            Log(process, "completed", out);
                            processQueue.remove();
                            break;
                        }
                        if(process.ioNextBlock == process.ioBlockInterval) {
                            Log(process, "I/O blocked", out);
                            process.timesBlocked++;
                            process.ioNextBlock = 0;
                            processQueue.add(processQueue.remove());
                            break;
                        }
                        if(process.quantum == 0) {
                            process.priority++;
                            Log(process, "demoted", out);
                            pendingProcessQueue.add(processQueue.remove());
                            break;
                        }
                        if(result.totalRunTime == maxRunTime) {
                            return result;
                        }
                    }
                }
                quantum *= 2;

                if(pendingProcessQueue.isEmpty()) {
                    return result;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return result;
    }
}

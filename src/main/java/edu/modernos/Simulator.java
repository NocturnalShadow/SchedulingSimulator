// This file contains the main() function for the Scheduling
// simulation.  Init() initializes most of the variables by
// reading from a provided file.  SchedulingAlgorithm.Run() is
// called from main() to run the simulation.  Summary-Results
// is where the summary results are written, and Summary-Processes
// is where the process scheduling summary is written.

// Created by Alexander Reeder, 2001 January 06
// Reworked by Bogdan Panchuk, 2017 October 28
//

package edu.modernos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.builder.fluent.Configurations;

public class Simulator {
    private static String configFileName = "scheduling.config";
    private static String summaryFileName;
    private static String logFileName;

    private static int totalRunTime;
    private static int runTimeAverage;
    private static int runTimeStandardDeviation;

    private static LinkedList<Process> processes = new LinkedList<>();

    private static void AddProcess(int ioBlockTime) {
        int cpuTime = (int) (Random.Generate() * runTimeStandardDeviation) + runTimeAverage;
        processes.add(new Process(cpuTime, ioBlockTime));
    }

    private static void Initialize() {
        try {
            Configurations configs      = new Configurations();
            Configuration config        = configs.properties(new File(configFileName));

            logFileName                 = config.getString("simulator.logFileName", "Log.txt");
            summaryFileName             = config.getString("simulator.summaryFileName", "Summary.txt");

            totalRunTime                = config.getInt("simulator.runTimeTotal", 1000);
            runTimeAverage              = config.getInt("processes.runTimeAverage", 1000);
            runTimeStandardDeviation    = config.getInt("processes.runTimeStandardDeviation", 100);

            int[] processes = config.get(int[].class, "process.ioBlockInterval");
            for(int ioBlockTime : processes) {
                AddProcess(ioBlockTime);
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        System.out.println("Working...");

        Initialize();

        Result result = Scheduler.Run(processes, totalRunTime, logFileName);

        try(PrintStream out = new PrintStream(new FileOutputStream(summaryFileName))) {
            out.println("Scheduling Type:       " + result.schedulingType);
            out.println("Scheduling Name:       " + result.schedulingName);
            out.println("Simulation Run Time:   " + result.totalRunTime);
            out.println("Average Run Time:      " + runTimeAverage);
            out.println("Standard Deviation:    " + runTimeStandardDeviation + "\r\n");

            String format = "%-10s%-12s%-16s%-16s%-16s%n";
            out.format(format, "Process", "CPU Time", "CPU Completed", "I/O Interval", "Times Blocked");

            int index = 1;
            for(Process process : processes) {
                out.format(format, index++,
                        process.cpuTimeNeeded   + " ms",
                        process.cpuTimeUsed     + " ms",
                        process.ioBlockInterval + " ms",
                        process.timesBlocked    + " times");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        System.out.println("Completed.");
    }
}


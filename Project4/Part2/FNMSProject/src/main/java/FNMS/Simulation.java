package FNMS;

import java.io.*;

// Class as a whole inspiration from Professors source code for Project 2
public class Simulation implements Utility {
    District district_ = new District();
    static int current_day_;

    public void RunSimulation(int n) {
        SetOutputStream();
        Print(" *** BEGINNING SIMULATION *** \n");
        // run however many days are input
        for (int i = 0; i < n; i++) {
            // iterate day and create daily logger
            current_day_++;
            Print(" ***SIMULATION : DAY " + current_day_ + " BEGINNING***");
            // run a day
            district_.RunDay();
            //tracker.ShowData();
            Print(" ***SIMULATION : DAY " + current_day_ + " HAS ENDED***\n");
        }  
        OutputResults();
    }

    private void SetOutputStream() {
        // make sure sim always starts at 0
        Simulation.current_day_ = 0;
        // set system out to Output.txt
        try {
            File file = new File("output/Output.txt");
            file.getParentFile().mkdirs();
            file.createNewFile();
            System.setOut(new PrintStream(file));
        } catch (IOException e) {
            Print("Error: Simulation could not set output to Output.txt");
            e.printStackTrace();
        }
    }

    private void OutputResults() {
        Print(" *** SIMULATION COMPLETE ***  OUTPUTTING RESULTS ***");
        district_.DisplayResults();
        Print("\n *** SIMULATION COMPLETE *** ");
    }
}
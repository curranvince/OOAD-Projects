import java.io.*;
// Takes inspiration from Professors source code for Project 2
public class Simulation implements Utility {
    Store store_ = new Store();
    static int current_day_;

    private void SetOutputStream() {
        // make sure sim starts at 0
        Simulation.current_day_ = 0;
        // set system out to Output.txt
        try {
            File file = new File("Output.txt");
            file.createNewFile();
            System.setOut(new PrintStream(file));
        } catch (IOException e) {
            Print("Error: Simulation could not set output to Output.txt");
            e.printStackTrace();
        }
    }

    private void OutputResults() {
        // display inventory & its value
        store_.DisplayInventory(true); 
        // display items sold & their value
        store_.DisplayInventory(false);
        // display money stats
        Print("The store has $" + store_.register_.GetAmount() + " in the register");
        Print("$" + store_.total_withdrawn_ + " was withdrawn from the bank");
    }

    public void RunSimulation(int n) {
        SetOutputStream();
        Print(" *** BEGINNING SIMULATION *** \n");
        // create Tracker
        Subscriber tracker = new Tracker();
        store_.Subscribe(tracker);
        // each loop represents one day
        for (int i = 0; i < n; i++) {
            // iterate day and create daily logger
            current_day_++;
            Print(" ***SIMULATION : DAY " + current_day_ + " BEGINNING***");
            Subscriber dailyLogger = new Logger(current_day_);
            store_.Subscribe(dailyLogger);
            if (current_day_ % 7 != 0) {
                store_.OpenToday();
            } else {
                // close the store on sundays
                store_.ClosedToday();
            }
            // close daily logger and show tracker at end of every day
            store_.Unsubscribe(dailyLogger);
            dailyLogger.Close();
            tracker.ShowData();
            Print(" ***SIMULATION : DAY " + current_day_ + " HAS ENDED***\n");
        }  
        store_.Unsubscribe(tracker);
        // display final results
        OutputResults();
        Print("\n *** SIMULATION COMPLETE *** ");
    }
}
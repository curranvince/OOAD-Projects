package FNMS;

import java.io.*;
import java.util.*;

// These Subscribers are an example of the Observer pattern
interface Subscriber extends Utility {
    public void Update(MyEvent event);
    public void OutputData();
    public void Close();
}

// Logger keeps track of all information for a single day and writes it to its own file
class Logger implements Subscriber { 
    // uses lazy instantiation
    private static Logger instance; 
    // current list of events and file writer
    private LinkedList<MyEvent> events_ = new LinkedList<MyEvent>();
    private FileWriter writer_;
    // what day logger thinks it is
    private int current_ = 0;
    
    private Logger() {}
    
    // return the single instance of logger
    public static Logger getInstance() {
        if (instance == null) { instance = new Logger(); }
        return instance;
    }

    // update data
    public void Update(MyEvent event) {
        boolean updated = false;
        for (MyEvent event_ : events_) {
            if (event_.equals(event)) {
                event_.update(event.GetData());
                updated = true;
            }
        }
        if (!updated) { events_.add(event); }
    }

    // make sure we write to appropriate file
    private void UpdateWriter() {
        // check if actual day is different from day logger thinks it is
        if (current_ != Simulation.current_day_) {
            // create file for current day https://www.w3schools.com/java/java_files_create.asp
            try {
                File file = new File("output/Logs/Logger-" + Simulation.current_day_ + ".txt");
                file.getParentFile().mkdirs(); //https://stackoverflow.com/questions/6142901/how-to-create-a-file-in-a-directory-in-java
                file.createNewFile();
                writer_ = new FileWriter(file);
            } catch (IOException e) {
                Print("Error: Logger failed to create file");
                e.printStackTrace();
            }
            current_ = Simulation.current_day_;
        }
    }

    // simplify write
    private void Write(String msg) {
        try {
            writer_.write(msg + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    // write data to file and clear it
    public void OutputData() {
        UpdateWriter();
        for (MyEvent event_ : events_) {
            Write(event_.toString());
        }
        events_.clear();
    }

    // close the writer at end of each run
    public void Close() {
        try {
            writer_.close();
        } catch (IOException e) {
            Print("Error: Failed to close Loggers file writer");
        }
    }
}

// Tracker exists for an entire simulation and 
// adds up cumulative stats for each clerk
class Tracker implements Subscriber {
    // Using eager instantiation
    private static final Tracker instance = new Tracker();
    private int[][] stats_ = new int[6][4];
    private String[] clerk_names_ = {"Velma","Daphne","Norville","Fred","Shaggy","Scooby"};
    // [][0] for days worked, [][1] for sold, [][2] for purchased, [][3] for damaged
    
    private Tracker() {};
    public static Tracker getInstance() { return instance; }
    
    // for data we're interested in, add to the data table
    public void Update(MyEvent event) {
        if (event instanceof ArrivalEvent) stats_[event.GetClerkID()][0] += event.GetData();
        else if (event instanceof BrokeTuningEvent) stats_[event.GetClerkID()][3] += event.GetData();
        else if (event instanceof ItemsSoldEvent) stats_[event.GetClerkID()][1] += event.GetData();
        else if (event instanceof ItemsBoughtEvent) stats_[event.GetClerkID()][2] += event.GetData();
        else if (event instanceof BrokeCleaningEvent) stats_[event.GetClerkID()][3] += event.GetData();
    }
    
    // print the data table
    public void OutputData() {
        Print("\nTracker : Day " + Simulation.current_day_);
        Print("Clerk      Days Worked       Items Sold      Items Purchased      Items Damaged ");
        for (int i = 0; i < clerk_names_.length; i++) {
            Print(clerk_names_[i] + "       " + stats_[i][0] + "                 " + stats_[i][1] + "               " + stats_[i][2] + "                    " + stats_[i][3]);
        }
        Print(""); //skips line
    }

    // clear data table on close
    public void Close() { stats_ = null; }
}
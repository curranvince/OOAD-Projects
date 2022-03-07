package FNMS;

import java.io.*;
import java.util.*;

// These Subscribers are an example of the Observer pattern
// They both also use the Singleton pattern
interface Subscriber extends Utility {
    static LinkedList<MyEvent> events_ = new LinkedList<MyEvent>();
    
    abstract public void UpdateData(); // called at end of each day
    abstract public void OutputData(); // called at end of simulation

    static void Update(MyEvent event) {
        boolean updated = false;
        for (MyEvent event_ : events_) {
            if (event_.equals(event)) {
                event_.UpdateData(event.GetData());
                updated = true;
                break;
            }
        }
        if (!updated) { events_.add(event); }
    }

    static void ClearEvents() { events_.clear(); } 
}

// Logger keeps track of all information for a single day and writes it to its own file
class Logger implements Subscriber { 
    // uses lazy instantiation
    private static Logger instance; 
    // current list of events and file writer
    
    private FileWriter writer_;
    // what day logger thinks it is
    private int current_ = 0;
    
    private Logger() {}
    
    // return the single instance of logger
    public static Logger getInstance() {
        if (instance == null) { instance = new Logger(); }
        return instance;
    }

    // write data to file and close writer at end of each day
    public void UpdateData() {
        UpdateWriter();
        for (MyEvent event : events_) {
            if (!(event instanceof CreatedClerkEvent || event instanceof EODRegisterEvent)) {
                Write(event.toString());
            }   
        }
        Close();
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

    // close the writer
    private void Close() {
        try {
            writer_.close();
        } catch (IOException e) {
            Print("Error: Failed to close Loggers file writer");
        }
    }

    public void OutputData() {} // do nothing at end of sim
}

// Tracker exists for an entire simulation and 
// adds up cumulative stats for each clerk
class Tracker implements Subscriber {
    // using eager instantiation
    private static final Tracker instance = new Tracker();
    private List<String> clerk_names_ = new ArrayList<String>();
    private int[][] stats_ = new int[6][4];
    // [][0] for days worked, [][1] for sold, [][2] for purchased, [][3] for damaged

    private Tracker() {};
    public static Tracker getInstance() { return instance; }
    public int[][] GetData() { return stats_; }
    public List<String> GetNames() { return clerk_names_; }
    
    // for data we're interested in, add to the data table
    public void UpdateData() {
        UpdateStats();
        Print("\nTracker : Day " + Simulation.current_day_);
        Print("Clerk      Days Worked       Items Sold      Items Purchased      Items Damaged ");
        for (int i = 0; i < clerk_names_.size(); i++) {
            Print(clerk_names_.get(i) + "       " + stats_[i][0] + "                 " + stats_[i][1] + "               " + stats_[i][2] + "                    " + stats_[i][3]);
        }
        Print(""); //skips line
    }

    private void UpdateStats() {
        for (MyEvent event : events_) {
            if (event instanceof CreatedClerkEvent) {
                if (!clerk_names_.contains(event.GetClerkName())) clerk_names_.add(event.GetClerkName());
            } else {
                if (event instanceof ArrivalEvent) stats_[event.GetClerkID()][0] += event.GetData();
                else if (event instanceof BrokeTuningEvent) stats_[event.GetClerkID()][3] += event.GetData();
                else if (event instanceof ItemsSoldEvent) stats_[event.GetClerkID()][1] += event.GetData();
                else if (event instanceof ItemsBoughtEvent) stats_[event.GetClerkID()][2] += event.GetData();
                else if (event instanceof BrokeCleaningEvent) stats_[event.GetClerkID()][3] += event.GetData();
            }
        }
    }
    
    public void OutputData() {} // do nothing at end of sim
}
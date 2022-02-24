package FNMS;

import java.io.*;
import java.util.*;

// These Subscribers are an example of the Observer pattern
interface Subscriber extends Utility {
    public void Update(String context, int data);
    public void OutputData();
    public void Close();
}

// Logger keeps track of all information for a single day and writes it to its own file
class Logger implements Subscriber { 
    // uses lazy instantiation
    private static Logger instance;
    private FileWriter writer_;
    private Map<String, Integer> data_ = new HashMap<String, Integer>(); // https://www.w3schools.com/java/java_hashmap.asp
    private Store current_store_ = null;
    private String name_;
    private int current_ = 0;

    private Logger() {}
    
    public static Logger getInstance() {
        if (instance == null) { instance = new Logger(); }
        return instance;
    }

    // make writing cleaner
    private void Write(String msg) { 
        try {
            writer_.write(msg + "\n");
        } catch (IOException e) {
            Print("Error: Logger failed to write to file");
            e.printStackTrace();
        } 
    }

    public void UpdateStore(Store store) { 
        current_store_ = store; 
        name_ = current_store_.GetActiveClerk().GetName();
    }

    // make sure we write to appropriate file
    private void UpdateWriter() {
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

    // update data
    public void Update(String context, int data) {
        UpdateWriter();
        if (data_.get(context) == null) {
            data_.put(context, data);
        } else {
            data_.put(context, (data_.get(context) + data)); // https://stackoverflow.com/questions/4157972/how-to-update-a-value-given-a-key-in-a-hashmap
        }
    }

    // write data to file and reset data
    public void OutputData() {
        for (String i : data_.keySet()) {
            switch (i) {
                case "arrival": 
                    Write(name_ + " arrived at the " + current_store_.getName());
                    break;
                case "itemsadded":
                    Write(name_ + " added " + data_.get("itemsadded") + " item(s) to " + current_store_.getName() + "'s inventory");
                    break;
                case "checkedregister":
                    Write(name_ + " checked register to find $" + data_.get("checkedregister"));
                    break;
                case "totalitems":
                    Write(name_ + " counted " + data_.get("totalitems") + " item(s) in inventory");
                    break;
                case "totalitemsprice":
                    Write(name_ + " found the store's inventory is worth $" + data_.get("totalitemsprice"));
                    break;
                case "brokeintuning":
                    Write(name_ + " damaged " + data_.get("brokeintuning") + " item(s) while tuning");
                    break;
                case "itemsordered":
                    Write(name_ + " ordered " + data_.get("itemsordered") + " item(s)");
                    break;
                case "itemsold":
                    Write(name_ + " sold " + data_.get("itemssold") + " item(s)");
                    break;
                case "itemsbought":
                    Write(name_ + " purchased " + data_.get("itemsbought") + " item(s)");
                    break;
                case "damagedcleaning":
                    Write(name_ + " damaged " + data_.get("damagedcleaning") + " item(s) while cleaning");
                    break;
                case "leftstore":
                    Write(name_ + " left the " + current_store_.getName());
                    break;
                case "closed":
                    Write("The " + current_store_.getName() + " was closed today");
                    break;
                default:
                    break;
            }
        }
        data_.clear();
    }

    // close the writer when logger closes
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
    private int clerk_index_;
    // [0][] for Shaggy, [1][] for Velma, [2][] for Daphne
    // [][0] for days worked, [][1] for sold, [][2] for purchased, [][3] for damaged
    
    private Tracker() {};
    public static Tracker getInstance() { return instance; }
    
    public void UpdateClerk(String name) {
        for (int i = 0; i < District.clerks_.size(); i++) {
            if (name == District.clerks_.get(i).GetName()) { clerk_index_ = i; }
        }
    }
    
    // for data we're interested in, add to the data table
    public void Update(String context, int data) {
        switch (context) {
            case "arrival": 
                stats_[clerk_index_][0] += data;
                break;
            case "brokeintuning":
                stats_[clerk_index_][3] += data;
                break;
            case "itemsold":
                stats_[clerk_index_][1] += data;
                break;
            case "itemsbought":
                stats_[clerk_index_][2] += data;
                break;
            case "damagedcleaning":
                stats_[clerk_index_][3] += data;
                break;
            default:
                break;
        }
    }

    // print the data table
    public void OutputData() {
        Print("\nTracker : Day " + Simulation.current_day_);
        Print("Clerk      Days Worked       Items Sold      Items Purchased      Items Damaged ");
        for (int i = 0; i < District.clerks_.size(); i++) {
            Print(District.clerks_.get(i).GetName() + "       " + stats_[i][0] + "                 " + stats_[i][1] + "               " + stats_[i][2] + "                    " + stats_[i][3]);
        }
        Print(""); //skips line
    }

    // clear data table on close
    public void Close() { stats_ = null; }
}
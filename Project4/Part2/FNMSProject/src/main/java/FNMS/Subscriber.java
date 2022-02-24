package FNMS;

import java.io.*;

// These Subscribers are an example of the Observer pattern
interface Subscriber extends Utility {
    public void Update(String context, String name, int data);
    public void ShowData();
    public void Close();
}

// Logger keeps track of all information for a single day and writes it to its own file
class Logger implements Subscriber { 
    // uses lazy instantiation
    private static Logger instance;
    private FileWriter writer_;
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

    // consume context/data and write it to file
    public void Update(String context, String clerk, int data) {
        UpdateWriter();
        switch (context) {
            case "arrival": 
                Write(clerk + " arrived at the store");
                break;
            case "itemsadded":
                Write(clerk + " added " + data + " item(s) to inventory");
                break;
            case "checkedregister":
                Write(clerk + " checked register to find $" + data);
                break;
            case "totalitems":
                Write(clerk + " counted " + data + " item(s) in inventory");
                break;
            case "totalitemsprice":
                Write(clerk + " found the store's inventory is worth $" + data);
                break;
            case "brokeintuning":
                Write(clerk + " damaged " + data + " item(s) while tuning");
                break;
            case "itemsordered":
                Write(clerk + " ordered " + data + " item(s)");
                break;
            case "itemsold":
                Write(clerk + " sold " + data + " item(s)");
                break;
            case "itemsbought":
                Write(clerk + " purchased " + data + " item(s)");
                break;
            case "damagedcleaning":
                Write(clerk + " damaged " + data + " item(s) while cleaning");
                break;
            case "leftstore":
                Write(clerk + " left the store");
                break;
            case "closed":
                Write("The store was closed today");
                break;
            default:
                break;
        }
    }

    // close the writer when logger closes
    public void Close() {
        try {
            writer_.close();
        } catch (IOException e) {
            Print("Error: Failed to close Loggers file writer");
        }
    }

    // holds no data
    public void ShowData() {}
}

// Tracker exists for an entire simulation and 
// adds up cumulative stats for each clerk
class Tracker implements Subscriber {
    // Using eager instantiation
    private static final Tracker instance = new Tracker();
    private int[][] stats_ = new int[6][4];
    // [0][] for Shaggy, [1][] for Velma, [2][] for Daphne
    // [][0] for days worked, [][1] for sold, [][2] for purchased, [][3] for damaged
    
    private Tracker() {};
    public static Tracker getInstance() { return instance; }
    
    // for data we're interested in, add to the data table
    public void Update(String context, String name, int data) {
        int clerk_index = -1;
        for (int i = 0; i < District.clerks_.size(); i++) {
            if (name == District.clerks_.get(i).GetName()) { clerk_index = i; }
        }
        if (clerk_index == -1) { Print("ERROR: TRACKER COULDNT FIND CLERK"); }
        switch (context) {
            case "arrival": 
                stats_[clerk_index][0] += data;
                break;
            case "brokeintuning":
                stats_[clerk_index][3] += data;
                break;
            case "itemsold":
                stats_[clerk_index][1] += data;
                break;
            case "itemsbought":
                stats_[clerk_index][2] += data;
                break;
            case "damagedcleaning":
                stats_[clerk_index][3] += data;
                break;
            default:
                break;
        }
    }

    // print the data table
    public void ShowData() {
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
package FNMS;

import java.util.*;

public class District implements Utility {
    Tracker tracker_ = new Tracker();
    public List<Store> stores_ = new ArrayList<Store>();
    
    static List<AbstractClerk> clerks_ = new ArrayList<AbstractClerk>();
    static List<Integer> unavailable_clerks_ = new ArrayList<Integer>();

    District() {
        // create stores and clerks
        stores_.add(new Store("North Side Store", new NorthKitFactory()));
        stores_.add(new Store("South Side Store", new SouthKitFactory()));
        GenerateClerks();
        // create Tracker
        for (int i = 0; i < stores_.size(); i++) {
            stores_.get(i).Subscribe(tracker_);
        }
    }
    
    private void GenerateClerks() {
        // make decorated clerks with break chances & tuning algorithms
        clerks_.add(new ClerkSellDecorator(new Clerk("Velma", 5, new ElectronicTune())));
        clerks_.add(new ClerkSellDecorator(new Clerk("Daphne", 10, new HaphazardTune())));
        clerks_.add(new ClerkSellDecorator(new Clerk("Norville", 15, new ManualTune())));
        clerks_.add(new Clerk("Fred", 15, new ManualTune()));
        clerks_.add(new Clerk("Shaggy", 20, new ElectronicTune()));
        clerks_.add(new Clerk("Scooby", 25, new HaphazardTune()));
    }

    public void RunDay() {
        // create daily logger and subscribe stores to it
        Subscriber dailyLogger = new Logger(Simulation.current_day_);
        for (int i = 0; i < stores_.size(); i++) {
            stores_.get(i).Subscribe(dailyLogger);
        }
        // open or close stores depending on day
        if (Simulation.current_day_ % 7 != 0) { 
            AssignClerks();
            for (int i = 0; i < stores_.size(); i++) {
                stores_.get(i).OpenToday();
            }
        } else { 
            ResetDaysWorked();
            for (int i = 0; i < stores_.size(); i++) {
                stores_.get(i).ClosedToday();
            }
        }
        // show tracker at end of each day
        tracker_.ShowData();
        // have stores unsubscribe from logger and close it
        for (int i = 0; i < stores_.size(); i++) {
            stores_.get(i).Unsubscribe(dailyLogger);
        }
        dailyLogger.Close();
    }

    private void AssignClerks() {
        // update whos available to work
        UpdateClerkStatus();
        // assign clerks to stores
        for (int i = 0; i < stores_.size(); i++) {
            int worker = GetRandomNum(clerks_.size());
            while (District.unavailable_clerks_.contains(worker)) {
                worker = GetRandomNum(clerks_.size());
            }
            stores_.get(i).UpdateClerk(clerks_.get(worker));
            District.unavailable_clerks_.add(worker);
        }
    }

    private void UpdateClerkStatus() {
        // reset whos available each day
        District.unavailable_clerks_ = new ArrayList<Integer>();
        // 10% chance for 1 or 2 clerks to be sick
        if (GetRandomNum(10) == 0) {
            int amount = GetRandomNum(1,3);
            for (int i = 0; i < amount; i++) {
                int cantWorkID = GetRandomNum(clerks_.size());
                District.unavailable_clerks_.add(cantWorkID);
                Print(clerks_.get(cantWorkID).GetName() + " is sick, so they can't work today");
            }
        }
        // check if clerks have worked 3 days in a row
        for (int i = 0; i < clerks_.size(); i++) {
            if (clerks_.get(i).GetDaysWorked() == 3) {
                District.unavailable_clerks_.add(i);
                Print(clerks_.get(i).GetName() + " has worked 3 days in a row, so they can't work today");
            }
        }
    }

    private void ResetDaysWorked() {
        for (int i = 0; i < clerks_.size(); i++) {
            clerks_.get(i).ResetDaysWorked();
        }
    }

    public void DisplayResults() {
        for (int i = 0; i < stores_.size(); i++) {
            Print("Results for " + stores_.get(i).name_);
            // display inventory & its value
            stores_.get(i).DisplayInventory(true); 
            // display items sold & their value
            stores_.get(i).DisplayInventory(false);
            // display money stats
            Print(stores_.get(i).name_ + " has $" + stores_.get(i).register_.GetAmount() + " in the register");
            Print("$" + stores_.get(i).total_withdrawn_ + " was withdrawn from the bank");
            stores_.get(i).Unsubscribe(tracker_);
        }
        tracker_.Close();
    }
}
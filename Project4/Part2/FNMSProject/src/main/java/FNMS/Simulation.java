package FNMS;

import java.util.*;
import java.io.*;

public class Simulation implements Utility {
    static int last_day_;
    static int current_day_;

    private List<Store> stores_ = new ArrayList<Store>();
    private List<AbstractClerk> clerks_ = new ArrayList<AbstractClerk>();
    private List<Integer> unavailable_clerks_ = new ArrayList<Integer>();
    private Scanner scanner_ = new Scanner(System.in);

    // generate stores and clerks on simulation instantiation
    public Simulation() {
        GenerateStores();
        GenerateClerks(); 
    }

    private void SetDaysToRun() {
        Print("How many days would you like to run the simulation? (10-30)");
        int daysToRun = scanner_.nextInt();
        scanner_.nextLine();
        Print(String.valueOf(daysToRun));
        last_day_ = daysToRun;
    }

    private int GetClerkID(String name) {
        for (int i = 0; i < clerks_.size(); i++) {
            if (clerks_.get(i).GetName() == name) return i;
        }
        return -1;
    }
    
    private void GenerateStores() {
        // make north and south stores with appropriate kit factories
        stores_.add(new Store("North Side Store", new NorthKitFactory()));
        stores_.add(new Store("South Side Store", new SouthKitFactory()));
        for (int i = 0; i < stores_.size(); i++) {
            stores_.get(i).Subscribe(Tracker.getInstance());
        }
    }

    private void GenerateClerks() {
        // make decorated clerks with break chances & tuning algorithms
        clerks_.add(new ClerkSellDecorator(new Clerk(0, "Velma", 5, new ElectronicTune())));
        clerks_.add(new ClerkSellDecorator(new Clerk(1, "Daphne", 10, new HaphazardTune())));
        clerks_.add(new ClerkSellDecorator(new Clerk(2, "Norville", 15, new ManualTune())));
        clerks_.add(new Clerk(3, "Fred", 15, new ManualTune()));
        clerks_.add(new Clerk(4, "Shaggy", 20, new ElectronicTune()));
        clerks_.add(new Clerk(5, "Scooby", 25, new HaphazardTune()));
        for (int i = 0; i < clerks_.size(); i++) {
            clerks_.get(i).Subscribe(Tracker.getInstance());
        }
    }

    public void RunSimulation() {
        SetDaysToRun();
        Print(" *** BEGINNING SIMULATION *** \n");
        // run however many days are input
        for (int i = 0; i < last_day_; i++) {
            // iterate day and create daily logger
            current_day_++;
            Print(" ***SIMULATION : DAY " + current_day_ + " BEGINNING***");
            // run a day
            SimDay();
            //tracker.ShowData();
            Print(" ***SIMULATION : DAY " + current_day_ + " HAS ENDED***\n");
        }  
        DisplayResults();
    }

    private void HandleUser() {
        User user = new User(this);
        user.MakeRequests();

    }

    private void SimDay() {
        // open new logger and subscribe everyone
        OpenLogger();
        // open or close stores depending on day
        if (Simulation.current_day_ % 7 != 0) { 
            AssignClerks();
            if (current_day_ != last_day_) QueueCustomers();
            // no loop because we want to alternate between stores
            stores_.get(0).GetActiveClerk().ArriveAtStore();
            stores_.get(1).GetActiveClerk().ArriveAtStore();
            if (!stores_.get(0).GetActiveClerk().CheckRegister()) stores_.get(0).GetActiveClerk().GoToBank();
            if (!stores_.get(1).GetActiveClerk().CheckRegister()) stores_.get(1).GetActiveClerk().GoToBank();
            stores_.get(0).GetActiveClerk().PlaceOrders(stores_.get(0).GetActiveClerk().DoInventory());
            stores_.get(1).GetActiveClerk().PlaceOrders(stores_.get(1).GetActiveClerk().DoInventory());
            if (current_day_ == last_day_) HandleUser();
            else {
                stores_.get(0).Opens();
                stores_.get(1).Opens();
            }
            stores_.get(0).GetActiveClerk().CleanStore();
            stores_.get(1).GetActiveClerk().CleanStore();
            stores_.get(0).GetActiveClerk().CloseStore();
            stores_.get(1).GetActiveClerk().CloseStore();
            Logger.getInstance().OutputData();
        } else { 
            ResetDaysWorked();
            for (int i = 0; i < stores_.size(); i++) {
                stores_.get(i).ClosedToday();
                Logger.getInstance().OutputData();
            }
        }
        // show tracker at end of each day
        Tracker.getInstance().OutputData();
        // have stores unsubscribe from logger and close it
        CloseLogger();
    }

    private void AssignClerks() {
        // update whos available to work
        UpdateClerkStatus();
        // assign clerks to stores
        for (int i = 0; i < stores_.size(); i++) {
            int worker = GetRandomNum(clerks_.size());
            while (unavailable_clerks_.contains(worker)) {
                worker = GetRandomNum(clerks_.size());
            }
            stores_.get(i).UpdateClerk(clerks_.get(worker));
            unavailable_clerks_.add(worker);
        }
    }

    private void UpdateClerkStatus() {
        // reset whos available each day
        unavailable_clerks_ = new ArrayList<Integer>();
        // 10% chance for 1 or 2 clerks to be sick
        if (GetRandomNum(10) == 0) {
            int amount = GetRandomNum(1,3);
            for (int i = 0; i < amount; i++) {
                int cantWorkID = GetRandomNum(clerks_.size());
                unavailable_clerks_.add(cantWorkID);
                Print(clerks_.get(cantWorkID).GetName() + " is sick, so they can't work today");
            }
        }
        // check if clerks have worked 3 days in a row
        for (int i = 0; i < clerks_.size(); i++) {
            if (clerks_.get(i).GetDaysWorked() == 3) {
                unavailable_clerks_.add(i);
                Print(clerks_.get(i).GetName() + " has worked 3 days in a row, so they can't work today");
            }
        }
    }

    private void QueueCustomers() {
        List<Customer> toServe = new ArrayList<Customer>();
        if (current_day_ == last_day_) {
            toServe.add(new User(this));
            stores_.get(1).QueueCustomers(toServe);
        } else {
            for (int i = 0; i < stores_.size(); i++) {
                // get random amounts of buyers and sellers in range
                int buyers = 2 + GetPoissonRandom(3);
                int sellers = GetRandomNum(1, 5);
                // create buyers and sellers
                for (int j = 0; j < buyers; j++) { toServe.add(new Buyer(stores_.get(i).GetActiveClerk())); }
                for (int k = 0; k < sellers; k++) { toServe.add(new Seller(stores_.get(i).GetActiveClerk())); }
                // shuffle vector so we get customers in random order
                Collections.shuffle(toServe);
                stores_.get(i).QueueCustomers(toServe);
                toServe.clear();
            }
        }
    }

    private void OpenLogger() {
        // create daily logger and subscribe stores to it
        for (int i = 0; i < stores_.size(); i++) {
            stores_.get(i).Subscribe(Logger.getInstance());
        }
        for (int i = 0; i < clerks_.size(); i++) {
            clerks_.get(i).Subscribe(Logger.getInstance());
        }
    }

    private void CloseLogger() {
        for (int i = 0; i < stores_.size(); i++) {
            stores_.get(i).Unsubscribe(Logger.getInstance());
        }
        for (int i = 0; i < clerks_.size(); i++) {
            clerks_.get(i).Unsubscribe(Logger.getInstance());
        }
        Logger.getInstance().Close();
    }

    private void ResetDaysWorked() {
        for (int i = 0; i < clerks_.size(); i++) {
            clerks_.get(i).ResetDaysWorked();
        }
    }

    private void DisplayResults() {
        Print(" *** SIMULATION COMPLETE ***  OUTPUTTING RESULTS ***");
        for (int i = 0; i < stores_.size(); i++) {
            Print("Results for " + stores_.get(i).getName());
            // display inventory & its value
            stores_.get(i).DisplayInventory(true); 
            // display items sold & their value
            stores_.get(i).DisplayInventory(false);
            // display money stats
            Print(stores_.get(i).getName() + " has $" + stores_.get(i).register_.GetAmount() + " in the register");
            Print("$" + stores_.get(i).getWithdrawn() + " was withdrawn from the bank");
            stores_.get(i).Unsubscribe(Tracker.getInstance());
        }
        Tracker.getInstance().Close();
        Print("\n *** SIMULATION COMPLETE *** ");
    }
    
    public Store GetStore() {
        Print("What store would you like to switch to?");
        String name = scanner_.nextLine();
        for (int i = 0; i < stores_.size(); i++) {
            if (stores_.get(i).getName().contains(name)) {
                Print("Switched to " + stores_.get(i).getName());
                return stores_.get(i);
            }
        }
        Print("Couldn't switch stores, please try again before making a different request");
        return null;
    }
}
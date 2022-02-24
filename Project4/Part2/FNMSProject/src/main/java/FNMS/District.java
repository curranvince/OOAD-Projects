package FNMS;

import java.util.*;

public class District implements Utility {
    public List<Store> stores_ = new ArrayList<Store>();
    
    static List<AbstractClerk> clerks_ = new ArrayList<AbstractClerk>();
    static List<Integer> unavailable_clerks_ = new ArrayList<Integer>();

    District() {
        // create stores and clerks, subscribing each to the tracker
        GenerateStores();
        GenerateClerks();        
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
        clerks_.add(new ClerkSellDecorator(new Clerk("Velma", 5, new ElectronicTune())));
        clerks_.add(new ClerkSellDecorator(new Clerk("Daphne", 10, new HaphazardTune())));
        clerks_.add(new ClerkSellDecorator(new Clerk("Norville", 15, new ManualTune())));
        clerks_.add(new Clerk("Fred", 15, new ManualTune()));
        clerks_.add(new Clerk("Shaggy", 20, new ElectronicTune()));
        clerks_.add(new Clerk("Scooby", 25, new HaphazardTune()));
        for (int i = 0; i < clerks_.size(); i++) {
            clerks_.get(i).Subscribe(Tracker.getInstance());
        }
    }

    private void QueueCustomers() {
        for (int i = 0; i < stores_.size(); i++) {
            List<Customer> toServe = new ArrayList<Customer>();
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

    public void SimDay() {
        // open new logger and subscribe everyone
        OpenLogger();
        
        // open or close stores depending on day
        if (Simulation.current_day_ % 7 != 0) { 
            AssignClerks();
            QueueCustomers();
            for (int i = 0; i < stores_.size(); i++) {
                Logger.getInstance().UpdateStore(stores_.get(i));
                Tracker.getInstance().UpdateClerk(stores_.get(i).GetActiveClerk().GetName());
                stores_.get(i).OpenToday();
                Logger.getInstance().OutputData();
            }
        } else { 
            ResetDaysWorked();
            for (int i = 0; i < stores_.size(); i++) {
                Logger.getInstance().UpdateStore(stores_.get(i));
                stores_.get(i).ClosedToday();
                Logger.getInstance().OutputData();
            }
        }
        // show tracker at end of each day
        Tracker.getInstance().OutputData();
        // have stores unsubscribe from logger and close it
        CloseLogger();
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
    }
}
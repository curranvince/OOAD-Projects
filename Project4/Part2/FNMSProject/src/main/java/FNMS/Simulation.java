package FNMS;

import java.util.*;

public class Simulation implements Utility {
    static int last_day_;
    static int current_day_;
    static int num_clerks_;

    private List<Store> stores_ = new ArrayList<Store>();
    private List<AbstractClerk> clerks_ = new ArrayList<AbstractClerk>();
    private List<Integer> unavailable_clerks_ = new ArrayList<Integer>();
    private SubMan sub_man_ = new SubMan();

    class SubMan {
        private List<Subscriber> subs = new ArrayList<Subscriber>();
        private List<Graph> graphs = new ArrayList<Graph>();

        public SubMan() {
            subs.add(Tracker.getInstance());
            subs.add(Logger.getInstance());
            graphs.add(MoneyGraph.getInstance());
            graphs.add(ItemGraph.getInstance());
            graphs.add(ComparisonGraph.getInstance());
            subs.addAll(graphs);
        }

        public void SubscribeAll(List<? extends Publisher> pubs) {
            for (Publisher pub : pubs) {
                for (Subscriber sub : subs) {
                    pub.Subscribe(sub);
                }
            }
        }

        public void HandleEOD() {
            Tracker.getInstance().OutputData();
            Logger.getInstance().OutputData();
            for (Graph graph : graphs) {
                graph.UpdateData();
            }
        }

        public void WriteGraphs() {
            for (Graph graph : graphs) {
                graph.OutputData();
            }
        }
    }

    // generate stores and clerks on simulation instantiation
    public Simulation() {
        GenerateStores();
        GenerateClerks(); 
        sub_man_.SubscribeAll(stores_);
        sub_man_.SubscribeAll(clerks_);
        SetDaysToRun();
    }

    private void SetDaysToRun() {
        Print("How many days would you like to run the simulation? (10-30)");
        last_day_ = GetIntFromUser(10,30);
    }

    private void GenerateStores() {
        // make north and south stores with appropriate kit factories
        stores_.add(new Store("North Side Store", new NorthKitFactory()));
        stores_.add(new Store("South Side Store", new SouthKitFactory()));
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
    
    public void RunSimulation() {
        Print(" *** SIMULATION BEGINNING *** \n");
        // run however many days are input
        for (int i = 0; i < last_day_; i++) {
            // iterate day and run a day in the simulation
            current_day_++;
            Print(" ***SIMULATION : DAY " + current_day_ + " BEGINNING***");
            SimDay();
            Print(" ***SIMULATION : DAY " + current_day_ + " HAS ENDED***\n");
        }  
        DisplayResults();
        Print("\n *** SIMULATION COMPLETE *** ");
    }

    private void SimDay() {
        // check if its sunday
        if (Simulation.current_day_ % 7 != 0) { 
            // if not sunday assign clerks and queue customers
            AssignClerks();
            if (current_day_ != last_day_) QueueCustomers();
            // interleave commands between stores
            // clerks arrive
            for (Store store : stores_) store.GetActiveClerk().ArriveAtStore();
            // clerks check register and go to bank if necessary
            for (Store store : stores_) if (!store.GetActiveClerk().CheckRegister()) store.GetActiveClerk().GoToBank();
            // clerks do inventory and place orders
            for (Store store : stores_) store.GetActiveClerk().PlaceOrders(store.GetActiveClerk().DoInventory());
            // let user go on last day, else go through regular customer line
            if (current_day_ == last_day_) {
                User user = new User(this);
                user.MakeRequests();
            } else {
                for (Store store : stores_) store.Opens();
            }
            // have clerks clean and close stores
            for (Store store : stores_) store.GetActiveClerk().CleanStore();
            for (Store store : stores_) store.GetActiveClerk().CloseStore();
        } else { 
            // on sundays reset all the clerks days worked and announce the stores are closed
            ResetDaysWorked();
            for (Store store : stores_) store.ClosedToday();
        }
        // reset logger, display tracker, etc. 
        sub_man_.HandleEOD();
    }

    // pick clerks and assign them to store
    private void AssignClerks() {
        // update whos available to work
        UpdateClerkStatus();
        List<Integer> working = new ArrayList<Integer>();
        // assign clerks to stores
        for (int i = 0; i < stores_.size(); i++) {
            int worker = GetRandomNum(clerks_.size());
            // if they cant work pick someone else
            while (unavailable_clerks_.contains(worker)) { worker = GetRandomNum(clerks_.size()); }
            working.add(worker);
            stores_.get(i).UpdateClerk(clerks_.get(worker));
            unavailable_clerks_.add(worker);
        }
        // ensure any clerk whos not working has their days worked reset
        for (int i = 0; i < clerks_.size(); i++) {
            if (!working.contains(i)) clerks_.get(i).ResetDaysWorked();
        }
    }

    // update which clerks cannot work
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

    // generate customers and queue them at the stores 
    private void QueueCustomers() {
        List<Customer> toServe = new ArrayList<Customer>();
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

    // reset all clerks days worked
    private void ResetDaysWorked() {
        for (int i = 0; i < clerks_.size(); i++) {
            clerks_.get(i).ResetDaysWorked();
        }
    }

    // display simulation results
    private void DisplayResults() {
        Print(" *** OUTPUTTING SIMULATION RESULTS ***");
        for (int i = 0; i < stores_.size(); i++) {
            Print("Results for " + stores_.get(i).getName());
            // display inventory & its value
            stores_.get(i).DisplayInventory(true); 
            // display items sold & their value
            stores_.get(i).DisplayInventory(false);
            // display money stats
            Print(stores_.get(i).getName() + " has $" + stores_.get(i).register_.GetAmount() + " in the register");
            Print("$" + stores_.get(i).getWithdrawn() + " was withdrawn from the bank");
        }
        // write graphs to files
        sub_man_.WriteGraphs();
    }
    
    // return a store based off user input (so user can switch stores)
    public Store GetStore() {
        Print("What store would you like to switch to?");
        for (int i = 0; i < stores_.size(); i++) {
            Print(String.valueOf(i) + ": " + stores_.get(i).getName());
        }
        return stores_.get(GetIntFromUser(0, (stores_.size()-1)));
    }
}
public class Simulation implements Utility {
    Store store_ = new Store();
    static int current_day_ = 0;

    private Staff GetClerk() { return store_.GetClerk(); }

    public void RunSimulation(int n) {
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
                // pick whos working
                store_.ChooseClerk();
                // accept deliveries
                GetClerk().ArriveAtStore();
                // check the register & go to bank if we're broke
                if (!GetClerk().CheckRegister()) { GetClerk().GoToBank(); }
                // do inventory and order items
                GetClerk().PlaceOrders(GetClerk().DoInventory());
                // run the store day
                store_.Open();
                // clean the store
                GetClerk().CleanStore();
                // end the day
                GetClerk().CloseStore();
            } else {
                // close the store on sundays
                store_.HandleSunday();
            }
            store_.Unsubscribe(dailyLogger);
            Print(" ***SIMULATION : DAY " + current_day_ + " HAS ENDED***\n");
        }  
        // display final results
        store_.OutputResults();
        store_.Unsubscribe(tracker);
        Print("\n *** SIMULATION COMPLETE *** ");
    }
}
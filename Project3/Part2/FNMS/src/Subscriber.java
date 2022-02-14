import java.io.*;
// These Subscribers are an example of the Observer pattern. 
interface Subscriber {
    public void Update(String context, Staff clerk, int data);
    public void ShowData();
}

class Logger implements Subscriber{ 
    private FileWriter writer_;
    private int current_day_;

    Logger(int current_day) {
        current_day_ = current_day;
        // create file for current day https://www.w3schools.com/java/java_files_create.asp
        try {
            File file = new File("Logs/Logger-" + current_day_ + ".txt");
            file.getParentFile().mkdirs(); //https://stackoverflow.com/questions/6142901/how-to-create-a-file-in-a-directory-in-java
            file.createNewFile();
            writer_ = new FileWriter(file);
        } catch (IOException e) {
            System.out.println("Error: Logger failed to create file");
            e.printStackTrace();
        }
    }

    private void Write(Staff clerk, String msg) { 
        try {
            writer_.write(clerk.name_ + " " + msg);
        } catch (IOException e) {
            System.out.println("Error: Logger failed to write to file");
            e.printStackTrace();
        } 
    }

    public void Update(String context, Staff clerk, int data) {
        // consume information
        switch (context) {
            case "arrival": 
                Write(clerk, "arrived at the store");
                break;
            case "itemsadded":
                Write(clerk, "added " + data + " items to inventory");
                break;
            case "checkedregister":
                Write(clerk, "checked register to find $" + data);
                break;
            case "totalitems":
                Write(clerk, "found " + data + " items in inventory");
                break;
            case "totalitemsprice":
                Write(clerk, "found our inventory is worth $" + data);
                break;
            case "brokeintuning":
                Write(clerk, "broke " + data + " items while tuning");
                break;
            case "itemsordered":
                Write(clerk, "ordered " + data + " items");
                break;
            case "itemsold":
                Write(clerk, "sold " + data + " items");
                break;
            case "itemsbought":
                Write(clerk, "purchased " + data + " items");
                break;
            case "damagedcleaning":
                Write(clerk, "broke " + data + " items while cleaning");
                break;
            case "leftstore":
                Write(clerk, "went home for the day");
                break;
            default:
                break;
        }
    }

    public void ShowData() {}
}

class Tracker implements Subscriber {
    int[][] stats_ = new int[3][];
    // [0][] for Velma, [1][] for Shaggy, [2][] for Daphne
    // [][0] for sold, [][1] for purchased, [][2] for damaged

    public void Update(String context, Staff clerk, int data) {
        int clerk_index = 0;
        if (clerk.name_ == "Shaggy") clerk_index = 1;
        else if (clerk.name_ == "Daphne") clerk_index = 2;

        switch (context) {
            case "brokeintuning":
                stats_[clerk_index][2] += data;
                break;
            case "itemsold":
                stats_[clerk_index][0] += data;
                break;
            case "itemsbought":
                stats_[clerk_index][1] += data;
                break;
            case "damagedcleaning":
                stats_[clerk_index][2] += data;
                break;
            default:
                break;
        }
    }

    public void ShowData() {
        System.out.println("Clerk      Items Sold      Items Purchased      Items Damaged ");
        System.out.println("Velma      " + stats_[0][0] + "      " + stats_[0][1] + "      " + stats_[0][2]);
        System.out.println("Shaggy      " + stats_[1][0] + "      " + stats_[1][1] + "      " + stats_[1][2]);
        System.out.println("Daphne      " + stats_[2][0] + "      " + stats_[2][1] + "      " + stats_[2][2]);
    }
}
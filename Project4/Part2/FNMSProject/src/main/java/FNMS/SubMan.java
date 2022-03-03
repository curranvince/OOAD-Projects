package FNMS;

import java.util.List;
import java.util.ArrayList;

public class SubMan {
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
        Logger.getInstance().Close();
        for (Graph graph : graphs) {
            graph.UpdateData();
        }
    }

    private void WriteGraphs() {
        for (Graph graph : graphs) {
            graph.OutputData();
        }
    }

    private void CloseAll() {
        Logger.getInstance().Close();
        Tracker.getInstance().Close();
        for (Graph graph : graphs) {
            graph.Close();
        }
    }

    public void Shutdown() {
        WriteGraphs();
        CloseAll();
    }
}
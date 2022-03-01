package FNMS;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.awt.Color;
import java.awt.Font;
import java.awt.BasicStroke;

abstract class Graph implements Subscriber {
    protected List<Class> interesting_events_ = new ArrayList<Class>();
    protected LinkedList<MyEvent> events_ = new LinkedList<MyEvent>();
    protected String fileName_;
    protected int current_ = 0;

    abstract protected void UpdateSeries();
    abstract protected XYSeriesCollection CreateDataSet();
    abstract protected JFreeChart CreateGraph();

    // keep list of events for each day
    final public void Update(MyEvent event) {
        // if day has changed, add data points and clear events
        if (current_ != Simulation.current_day_) { UpdateSeries(); }
        for (Class clazz : interesting_events_) {
            if (clazz == event.getClass()) {
                boolean updated = false;
                // add event if needed, else update whats there
                for (MyEvent event_ : events_) {
                    if (event_.equals(event)) {
                        event_.update(event.GetData());
                        updated = true;
                    }
                }
                if (!updated) { events_.add(event); }
            }
        }
    }

    protected void FormatPlot(XYPlot plot) {
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        // set line color and size
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        // set background color and gridlines
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);
    }

    public void OutputData() {
        JFreeChart chart = CreateGraph();
        try {
            File file = new File("output/Graphs/" + fileName_ + ".png");
            file.getParentFile().mkdirs();
            file.createNewFile();
            OutputStream stream = new FileOutputStream(file);
            ChartUtils.writeChartAsPNG(stream, chart, 450, 400);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// https://zetcode.com/java/jfreechart/
// https://www.javatips.net/blog/create-line-chart-using-jfreechart
// https://stackoverflow.com/questions/34836338/how-to-save-current-chart-in-chartpanel-as-png-programmatically
class MoneyGraph extends Graph {
    // eagerly instantiated singleton
    private static final MoneyGraph instance = new MoneyGraph();

    private XYSeries money_ = new XYSeries("Register");
    private XYSeries sales_ = new XYSeries("Item Sales");
    
    private MoneyGraph() {
        interesting_events_.add(EODRegisterEvent.class);
        interesting_events_.add(SalePriceEvent.class);
        fileName_ = "MoneyGraph";
    };

    public static MoneyGraph getInstance() { return instance; }

    // add the previous days data points to the series & iterate day
    protected void UpdateSeries() {
        int money, sales;
        money = sales = 0;
        for (MyEvent event : events_) {
            if (event instanceof EODRegisterEvent) {
                money += event.GetData();
            } else if (event instanceof SalePriceEvent) {
                sales += event.GetData();
            }
        }
        money_.add(current_, money);
        sales_.add(current_, sales);
        // clear events to be ready for next day
        events_.clear();
        current_++;
    }

    protected XYSeriesCollection CreateDataSet() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(money_);
        dataset.addSeries(sales_);
        return dataset;
    }

    // create graph from all collected data
    protected JFreeChart CreateGraph() {
        // create chart
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Money Stats",
            "Day",
            "Amount ($)",
            CreateDataSet(),
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        // format and return chart
        FormatPlot(chart.getXYPlot());
        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.setTitle(new TextTitle("Money Stats (Both Stores)",
                        new Font("Serif", java.awt.Font.BOLD, 18)
                )
        );
        return chart;
    }

    public void Close() {};
}

class ItemGraph extends Graph {
    // eagerly instantiated singleton
    private static final ItemGraph instance = new ItemGraph();

    private XYSeries inventory_ = new XYSeries("Items in Inventory");
    private XYSeries damaged_ = new XYSeries("Items Damaged");
    private XYSeries sold_ = new XYSeries("Items Sold");
    
    private ItemGraph() {
        interesting_events_.add(InventoryEvent.class);
        interesting_events_.add(BrokeTuningEvent.class);
        interesting_events_.add(BrokeCleaningEvent.class);
        interesting_events_.add(ItemsSoldEvent.class);
        fileName_ = "ItemGraph";
    };

    public static ItemGraph getInstance() { return instance; }

    // add the previous days data points to the series & iterate day
    protected void UpdateSeries() {
        int inventory, damaged, sold;
        inventory = damaged = sold = 0;
        for (MyEvent event : events_) {
            if (event instanceof InventoryEvent) {
                inventory += event.GetData();
            } else if (event instanceof BrokeTuningEvent || event instanceof BrokeCleaningEvent) {
                damaged += event.GetData();
            } else if (event instanceof ItemsSoldEvent) {
                sold += event.GetData();
            }
        }
        inventory_.add(current_, inventory);
        damaged_.add(current_, damaged);
        sold_.add(current_, sold);
        // clear events to be ready for next day
        events_.clear();
        current_++;
    }

    protected XYSeriesCollection CreateDataSet() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(inventory_);
        dataset.addSeries(damaged_);
        dataset.addSeries(sold_);
        return dataset;
    }

    // create graph from all collected data
    protected JFreeChart CreateGraph() {
        // create chart
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Item Stats",
            "Day",
            "Amount ($)",
            CreateDataSet(),
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        // format and return chart
        FormatPlot(chart.getXYPlot());
        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.setTitle(new TextTitle("Item Stats (Both Stores)",
                        new Font("Serif", java.awt.Font.BOLD, 18)
                )
        );
        return chart;
    }

    public void Close() {};
}

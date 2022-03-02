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

// the Graph class is an example of a template
// https://zetcode.com/java/jfreechart/
// https://www.javatips.net/blog/create-line-chart-using-jfreechart
// https://stackoverflow.com/questions/34836338/how-to-save-current-chart-in-chartpanel-as-png-programmatically
abstract class Graph implements Subscriber {
    static final int HEIGHT = 450;
    static final int WIDTH = 450;

    protected List<Class> interesting_events_ = new ArrayList<Class>(); // class types of events we're interested in
    protected LinkedList<MyEvent> events_ = new LinkedList<MyEvent>();  // current list of events
    protected String[] graphName_ = new String[3];    // 0 for title, 1 for x title, 2 for y title
    protected String fileName_;                       // file to print to

    abstract protected JFreeChart CreateGraph();

    // keep list of events for each day
    final public void Update(MyEvent event) {
        // if day has changed, add data points and clear events
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

    // output graph as .png
    final public void OutputData() {
        JFreeChart chart = CreateGraph();
        try {
            File file = new File("output/Graphs/" + fileName_ + ".png");
            file.getParentFile().mkdirs();
            file.createNewFile();
            OutputStream stream = new FileOutputStream(file);
            ChartUtils.writeChartAsPNG(stream, chart, HEIGHT, WIDTH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Close() {
        events_.clear();
    }
}

abstract class LineGraph extends Graph {
    protected List<XYSeries> series_ = new ArrayList<XYSeries>();       // series to be plotted
    
    abstract public void UpdateSeries();     // to update the line
    
    // fill in missing data & turn series into a dataset
    private XYSeriesCollection CreateDataSet() {
        NormalizeData();
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (XYSeries series : series_) {
            dataset.addSeries(series);
        }
        return dataset;
    }

    // create graph from all collected data
    protected JFreeChart CreateGraph() {
        // create chart
        JFreeChart chart = ChartFactory.createXYLineChart(
            graphName_[0],
            graphName_[1],
            graphName_[2],
            CreateDataSet(),
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        // format and return chart
        FormatPlot(chart.getXYPlot());
        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.setTitle(new TextTitle(graphName_[0] + " (Both Stores)",
                        new Font("Serif", java.awt.Font.BOLD, 18)
                )
        );
        return chart;
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

    protected void NormalizeData() {
        // fill in data for sundays with previous days money
        for (int i = 2; i < Simulation.last_day_; i++) {
            if (i % 7 == 0) {
                series_.get(0).update(i, series_.get(0).getY(series_.get(0).indexOf(i-1)));
            }
        }
    }

    @Override
    public void Close() {
        super.Close();
        series_.clear();
    }
}

class MoneyGraph extends LineGraph {
    // eagerly instantiated singleton
    private static final MoneyGraph instance = new MoneyGraph();
    
    private MoneyGraph() {
        series_.add(new XYSeries("Register"));
        series_.add(new XYSeries("Item Sales"));
        interesting_events_.add(EODRegisterEvent.class);
        interesting_events_.add(SalePriceEvent.class);
        fileName_ = "MoneyGraph";
        graphName_[0] = "Money Graph";
        graphName_[1] = "Days";
        graphName_[2] = "Amount ($)";
    };

    public static MoneyGraph getInstance() { return instance; }

    // add the previous days data points to the series & iterate day
    public void UpdateSeries() {
        int money, sales;
        money = sales = 0;
        for (MyEvent event : events_) {
            if (event instanceof EODRegisterEvent) {
                money += event.GetData();
            } else if (event instanceof SalePriceEvent) {
                sales += event.GetData();
            }
        }
        series_.get(0).add(Simulation.current_day_, money);
        series_.get(1).add(Simulation.current_day_, sales);
        // clear events to be ready for next day
        events_.clear();
    }
}

class ItemGraph extends LineGraph {
    // eagerly instantiated singleton
    private static final ItemGraph instance = new ItemGraph();
    
    private ItemGraph() {
        series_.add(new XYSeries("In Inventory"));
        series_.add(new XYSeries("Damaged"));
        series_.add(new XYSeries("Sold"));
        interesting_events_.add(InventoryEvent.class);
        interesting_events_.add(BrokeTuningEvent.class);
        interesting_events_.add(BrokeCleaningEvent.class);
        interesting_events_.add(ItemsSoldEvent.class);
        fileName_ = "ItemGraph";
        graphName_[0] = "Item Graph";
        graphName_[1] = "Days";
        graphName_[2] = "# of Items";
    };

    public static ItemGraph getInstance() { return instance; }

    // add the previous days data points to the series & iterate day
    public void UpdateSeries() {
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
        series_.get(0).add(Simulation.current_day_, inventory);
        series_.get(1).add(Simulation.current_day_, damaged);
        series_.get(2).add(Simulation.current_day_, sold);
        // clear events to be ready for next day
        events_.clear();
    }
}
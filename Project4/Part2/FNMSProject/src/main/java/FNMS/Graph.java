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
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.Color;
import java.awt.Font;

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

    abstract public void UpdateData();
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
    protected List<XYSeries> series_ = new ArrayList<XYSeries>(); // list of series to plot
    
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
        chart.setTitle(new TextTitle(graphName_[0],
                        new Font("Serif", java.awt.Font.BOLD, 18)
                )
        );
        return chart;
    }

    protected void FormatPlot(XYPlot plot) {
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
    public static MoneyGraph getInstance() { return instance; }
    
    private MoneyGraph() {
        series_.add(new XYSeries("Register"));
        series_.add(new XYSeries("Item Sales"));
        interesting_events_.add(EODRegisterEvent.class);
        interesting_events_.add(SalePriceEvent.class);
        fileName_ = "MoneyGraph";
        graphName_[0] = "Money Graph (Both Stores)";
        graphName_[1] = "Days";
        graphName_[2] = "Amount ($)";
    };

    // add the previous days data points to the series & iterate day
    public void UpdateData() {
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
    public static ItemGraph getInstance() { return instance; }

    private ItemGraph() {
        series_.add(new XYSeries("In Inventory"));
        series_.add(new XYSeries("Damaged"));
        series_.add(new XYSeries("Sold"));
        interesting_events_.add(InventoryEvent.class);
        interesting_events_.add(BrokeTuningEvent.class);
        interesting_events_.add(BrokeCleaningEvent.class);
        interesting_events_.add(ItemsSoldEvent.class);
        fileName_ = "ItemGraph";
        graphName_[0] = "Item Graph (Both Stores)";
        graphName_[1] = "Days";
        graphName_[2] = "# of Items";
    };

    // add the previous days data points to the series & iterate day
    public void UpdateData() {
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

class ComparisonGraph extends LineGraph {
    // eagerly instantiated singleton
    private static final ComparisonGraph instance = new ComparisonGraph();
    public static ComparisonGraph getInstance() { return instance; }

    private ComparisonGraph() {
        series_.add(new XYSeries("North Register"));
        series_.add(new XYSeries("North Sales"));
        series_.add(new XYSeries("South Register"));
        series_.add(new XYSeries("South Sales"));
        interesting_events_.add(EODRegisterEvent.class);
        interesting_events_.add(SalePriceEvent.class);
        fileName_ = "ComparisonGraph";
        graphName_[0] = "Comparison Graph";
        graphName_[1] = "Days";
        graphName_[2] = "Amount ($)";
    };

    // add the previous days data points to the series & iterate day
    public void UpdateData() {
        int n_mon, s_mon, n_sales, s_sales;
        n_mon = s_mon = n_sales = s_sales = 0;
        for (MyEvent event : events_) {
            if (event instanceof EODRegisterEvent) {
                if (event.GetStore().getName().contains("North")) {
                    n_mon += event.GetData();
                } else {
                    s_mon += event.GetData();
                }
            } else if (event instanceof SalePriceEvent) {
                if (event.GetStore().getName().contains("North")) {
                    n_sales += event.GetData();
                } else {
                    s_sales += event.GetData();
                }
            }
        }
        series_.get(0).add(Simulation.current_day_, n_mon);
        series_.get(1).add(Simulation.current_day_, n_sales);
        series_.get(2).add(Simulation.current_day_, s_mon);
        series_.get(3).add(Simulation.current_day_, s_sales);
        // clear events to be ready for next day
        events_.clear();
    }

    @Override
    protected void NormalizeData() {
        super.NormalizeData();
        // fill in data for sundays with previous days money
        for (int i = 2; i < Simulation.last_day_; i++) {
            if (i % 7 == 0) {
                series_.get(2).update(i, series_.get(2).getY(series_.get(2).indexOf(i-1)));
            }
        }
    }

    // https://stackoverflow.com/questions/21427762/jfreechart-set-line-colors-for-xy-chart-4-series-2-datasets-dual-axes
    // https://www.jfree.org/forum/viewtopic.php?t=2411
    @Override
    protected void FormatPlot(XYPlot plot) {
        super.FormatPlot(plot);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        // set color and shape to distinguish between stores
        for (int i = 0; i < 2; i++) {
            renderer.setSeriesPaint(i, Color.RED);
            renderer.setSeriesShape(i, new Rectangle2D.Double(-3.0, -3.0, 6.0, 6.0));
        }
        for (int i = 2; i < 4; i++) {
            renderer.setSeriesPaint(i, Color.BLUE);
            renderer.setSeriesShape(i, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
        }
        plot.setRenderer(renderer);
    }
}
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
import java.awt.Color;
import java.awt.Font;
import java.awt.BasicStroke;

// https://zetcode.com/java/jfreechart/
// https://www.javatips.net/blog/create-line-chart-using-jfreechart
// https://stackoverflow.com/questions/34836338/how-to-save-current-chart-in-chartpanel-as-png-programmatically
abstract class LineGraph {
    protected List<Store> stores_ = new ArrayList<Store>();

    abstract public void CollectData();
    abstract public void OutputGraph();
    abstract protected JFreeChart CreateGraph();

    // default constructor to set stores
    public void SetStores(List<Store> stores) {
        for (Store store : stores) stores_.add(store);
    }
}

class MoneyGraph extends LineGraph {
    //private XYSeries item_sales_ = new XYSeries("Item Sales");
    private XYSeries register_money_ = new XYSeries("In Register");
   
    public MoneyGraph() {};
    
    public void CollectData() {
        int register_money = 0;
        for (Store store : stores_) {
            register_money += store.register_.GetAmount();
        }
        //item_sales_.add(Simulation.current_day_, );
        register_money_.add(Simulation.current_day_, register_money);
    }

    protected JFreeChart CreateGraph() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(register_money_);
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Money Stats",
            "Day",
            "Amount ($)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("$ in Cash Registers",
                        new Font("Serif", java.awt.Font.BOLD, 18)
                )
        );

        return chart;
    }

    public void OutputGraph() {
        JFreeChart chart = CreateGraph();
        try {
            File file = new File("output/Graphs/MoneyGraph.png");
            file.getParentFile().mkdirs();
            file.createNewFile();
            OutputStream stream = new FileOutputStream(file);
            ChartUtils.writeChartAsPNG(stream, chart, 450, 400);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/*
class ItemGraph {

    void OutputGraph() {
        try {
            File file = new File("output/Graphs/ItemGraph.png");
            file.getParentFile().mkdirs();
            file.createNewFile();
            OutputStream stream = new FileOutputStream(file);
            ChartUtilities.writeChartAsPNG(stream, chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
*/
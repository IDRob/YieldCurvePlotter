package display.plotter;

import java.awt.Color;
import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import rates.Curves;

/**
 * This class plots a graph using the J Free Chart library.
 * <p>
 * Adapted from <a href="https://www.tutorialspoint.com/jfreechart/pdf/jfreechart_xy_chart.pdf">...</a>
 */
public class PlotGraphJFree extends ApplicationFrame  {

    /**
     * Initializes the plotted graph.
     *
     * @param applicationTitle the title of the application
     * @param chartTitle the title of the chart
     * @param curves the curves object to be plotted
     */
    public PlotGraphJFree( String applicationTitle, String chartTitle , Curves curves) {
        super(applicationTitle);
        JFreeChart chart = ChartFactory.createXYLineChart(
                chartTitle ,
                "Future Monthly Periods" ,
                "Interest Rate" ,
                createDataset(curves) ,
                PlotOrientation.VERTICAL ,
                true , true , false);

        ChartPanel chartPanel = new ChartPanel( chart );
        //chartPanel.setPreferredSize( new java.awt.Dimension( 1024, 1000 ) );
        final XYPlot plot = chart.getXYPlot( );

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        renderer.setSeriesPaint( 0 , Color.RED );
        renderer.setSeriesPaint( 1 , Color.GREEN );
        renderer.setSeriesStroke( 0 , new BasicStroke( 0.5f ) );
        renderer.setSeriesStroke( 1 , new BasicStroke( 0.5f ) );

        List<Double> allValues = getAllValues(curves);
        double min = allValues.get(0)*0.95;
        double max = getAllValues(curves).get(allValues.size() - 1)*1.05;

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setAutoRange(false);
        rangeAxis.setRange(min, max);

        plot.setRenderer( renderer );
        setContentPane( chartPanel );
    }

    private XYDataset createDataset(Curves curves) {
        final XYSeries parCurves = new XYSeries( "Discrete Tenor Par Curve" );
        curves.getParCurve().getMonthToRateCurve().forEach(parCurves::add);


        final XYSeries zeroCurves = new XYSeries( "Continuous Monthly Zero-Rates" );
        curves.getZeroCurve().getMonthToRateCurve().forEach(zeroCurves::add);

        final XYSeriesCollection dataset = new XYSeriesCollection( );
        dataset.addSeries( parCurves );
        dataset.addSeries( zeroCurves );
        return dataset;
    }

    private List<Double> getAllValues(Curves curves) {
        List<Double> parValues = new ArrayList<>(curves.getParCurve().getMonthToRateCurve().values());
        List<Double> zeroValues = new ArrayList<>(curves.getZeroCurve().getMonthToRateCurve().values());
        parValues.addAll(zeroValues);
        parValues.sort(Comparator.naturalOrder());
        return parValues;
    }

}

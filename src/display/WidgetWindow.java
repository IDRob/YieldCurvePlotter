package display;

import csvwriter.CsvWriter;
import datareader.UsTreasuryDataReader;
import numericalsolutions.interpolator.Interpolator;
import org.jfree.ui.RefineryUtilities;
import org.w3c.dom.Document;
import display.plotter.PlotGraphJFree;
import rates.Curves;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.NavigableMap;

/**
 * This class holds the yield curve plotter widget.
 */
public class WidgetWindow {

    /**
     * Widget lets user choose between available dates, show graph and save csv to downloads.
     *
     * @param usTreasuryRateData the xml US Treasury Bill data document
     * @param precision the required precision for the produced zero curve
     * @param interpolator the interpolator used to estimate missing zero curve values
     */
    public static void runWidget(Document usTreasuryRateData, double precision, Interpolator interpolator) {

        UsTreasuryDataReader usTreasuryDataReader = new UsTreasuryDataReader();
        List<LocalDate> dates = usTreasuryDataReader.getDates(usTreasuryRateData);

        JFrame frame = new JFrame("Yield Curve Plotter");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocation(430, 100);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // added code

        frame.add(panel);
        JLabel lbl = new JLabel("Select an available date");
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lbl);
        final JComboBox<LocalDate> cb = new JComboBox<>(dates.toArray(LocalDate[]::new));

        cb.setMaximumSize(cb.getPreferredSize());
        cb.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(cb);

        JButton btn = new JButton("Show Graph");
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(btn);

        JButton downloadBtn = new JButton("Save CSV to Download Folder");
        downloadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(downloadBtn);

        frame.setVisible(true);

        btn.addActionListener(e -> {
            LocalDate selectedDate =(LocalDate) cb.getSelectedItem();

            if (selectedDate != null) {
                NavigableMap<Integer, Double> dateSpecificParData =
                        usTreasuryDataReader.getsParDataForDate(selectedDate, usTreasuryRateData);

                Curves curves = Curves.of(precision, interpolator, dateSpecificParData);

                PlotGraphJFree chart = new PlotGraphJFree("Yield Curve Plotter",
                        "Treasury Yield Curves: " + selectedDate, curves);
                chart.pack();
                chart.setVisible( true );
                RefineryUtilities.centerFrameOnScreen( chart );
            }

        });

        downloadBtn.addActionListener(e -> {
            LocalDate selectedDate =(LocalDate) cb.getSelectedItem();

            if (selectedDate != null) {
                NavigableMap<Integer, Double> dateSpecificParData =
                        usTreasuryDataReader.getsParDataForDate(selectedDate, usTreasuryRateData);

                Curves curves = Curves.of(precision, interpolator, dateSpecificParData);

                CsvWriter.writeCurvesToCsv(curves, selectedDate);
            }
        });
    }
}

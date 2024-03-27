package display;

import csvwriter.CsvWriter;
import datareader.UsTreasuryDataReader;
import datareader.test.UsTreasuryDataReaderTest;
import numericalsolutions.interpolator.Interpolator;
import numericalsolutions.interpolator.LogarithmicInterpolator;
import org.w3c.dom.Document;
import display.plotter.PlotGraphJFree;
import rates.Curves;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.NavigableMap;

/**
 * This class holds the yield curve plotter widget.
 */
public class WidgetWindow extends JFrame{

    /**
     * Widget lets user choose between available dates, show graph and save csv to downloads.
     */
    public static void runWidget() {

        double precision = 1E-20;
        Interpolator interpolator = new LogarithmicInterpolator();

        JFrame frame = new JFrame("Yield Curve Plotter");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 1000);
        frame.setLocation(0, 0);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // added code
        frame.add(panel);

        // Uncomment to use saved 2023 data
        //Document usTreasuryRateData = UsTreasuryDataReaderTest.getDocument("TreasuryData.xml");

        // Uncomment to use live 2024 data
        Document usTreasuryRateData = UsTreasuryDataReader.getUSTreasuryRateData("2024");

        UsTreasuryDataReader usTreasuryDataReader = new UsTreasuryDataReader();
        JLabel dateLabel = new JLabel("Select an available date");
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(dateLabel);

        List<LocalDate> dates = usTreasuryDataReader.getDates(usTreasuryRateData);
        LocalDate[] datesArray = new LocalDate[dates.size()];
        datesArray = dates.toArray(datesArray);
        final JComboBox<LocalDate> dateDropDown = new JComboBox<>(datesArray);

        dateDropDown.setMaximumSize(dateDropDown.getPreferredSize());
        dateDropDown.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(dateDropDown);

        JButton showGraphBtn = new JButton("Show Graph");
        showGraphBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(showGraphBtn);

        JButton downloadBtn = new JButton("Save CSV to Download Folder");
        downloadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(downloadBtn);

        frame.setVisible(true);

        showGraphBtn.addActionListener(showGraphPress -> {
            LocalDate selectedDate =(LocalDate) dateDropDown.getSelectedItem();

            if (selectedDate != null) {
                NavigableMap<Integer, Double> dateSpecificParData =
                        usTreasuryDataReader.getsParDataForDate(selectedDate, usTreasuryRateData);

                Curves curves = Curves.of(precision, interpolator, dateSpecificParData);

                PlotGraphJFree chart = new PlotGraphJFree("Yield Curve Plotter",
                        "Treasury Yield Curves: " + selectedDate, curves);

                chart.setSize(500, 1000);
                chart.setLocation(500, 0);
                chart.pack();
                chart.setVisible( true );
            }

        });

        downloadBtn.addActionListener(downloadPress -> {
            LocalDate selectedDate =(LocalDate) dateDropDown.getSelectedItem();

            if (selectedDate != null) {
                NavigableMap<Integer, Double> dateSpecificParData =
                        usTreasuryDataReader.getsParDataForDate(selectedDate, usTreasuryRateData);

                Curves curves = Curves.of(precision, interpolator, dateSpecificParData);

                CsvWriter.writeCurvesToCsv(curves, selectedDate);
            }
        });
    }
}

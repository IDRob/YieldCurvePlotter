import datareader.UsTreasuryDataReader;
import datareader.test.UsTreasuryDataReaderTest;
import display.WidgetWindow;
import numericalsolutions.interpolator.Interpolator;
import numericalsolutions.interpolator.LogarithmicInterpolator;
import org.w3c.dom.Document;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        double precision = 0.000001;
        Interpolator interpolator = new LogarithmicInterpolator();

        //Document usTreasuryRateData = UsTreasuryDataReaderTest.getDocument("TreasuryData.xml"); // Uncomment to use saved 2023 data

        Document usTreasuryRateData = UsTreasuryDataReader.getUSTreasuryRateData("2024"); // Uncomment to use live 2024 data

        WidgetWindow.runWidget(usTreasuryRateData, precision, interpolator);
    }
}
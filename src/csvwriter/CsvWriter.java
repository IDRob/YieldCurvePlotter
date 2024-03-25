package csvwriter;

import rates.Curves;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.util.Map;
import java.util.NavigableMap;

/**
 * This class writes curve data to csv file and saves in the download folder.
 */
public class CsvWriter {

    /**
     * The system end of line separator.
     */
    static String EOL = System.lineSeparator();

    /**
     * Writes Treasury bills Curves data to a csv file.
     *
     * @param curves the curves to be written
     * @param curvesDate the date of the curve data
     */
    public static void writeCurvesToCsv(Curves curves, LocalDate curvesDate) {

        NavigableMap<Integer, Double> parCurve = curves.getParCurve().getMonthToRateCurve();
        NavigableMap<Integer, Double> zeroCurve = curves.getZeroCurve().getMonthToRateCurve();

        String home = System.getProperty("user.home");
        String fileName = "TreasuryBillYields" + curvesDate.toString();

        try (Writer writer = new FileWriter(home +"/Downloads/"+ fileName + ".csv")) {
            writer.append("Months")
                    .append(',')
                    .append("Discrete Tenor Par Curve")
                    .append(',')
                    .append("Continuous Monthly Zero Rates")
                    .append(EOL);

            for (Map.Entry<Integer, Double> entry : zeroCurve.entrySet()) {
                int zeroCurveMonth = entry.getKey();
                writer.append(String.valueOf(zeroCurveMonth))
                        .append(',');
                if (parCurve.containsKey(zeroCurveMonth)) {
                    writer.append(String.valueOf(parCurve.get(zeroCurveMonth)))
                            .append(',');
                } else {
                    writer.append(',');
                }

                writer.append(String.valueOf(entry.getValue()))
                        .append(EOL);
            }
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

}

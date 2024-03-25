package datareader.test;

import datareader.UsTreasuryDataReader;
import numericalsolutions.interpolator.Interpolator;
import numericalsolutions.interpolator.LogarithmicInterpolator;
import org.junit.Test;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import rates.Curves;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

/**
 * The test class for UsTreasuryDataReader.
 */
public class UsTreasuryDataReaderTest {

    /**
     * Treasury Bills Yield Data for 2023, for use when testing.
     */
    Document DOCUMENT = getDocument("TreasuryData.xml");

    @Test
    public void testGetDates() {
        List<LocalDate> dates = new UsTreasuryDataReader().getDates(DOCUMENT);
        assertEquals( 250, dates.size());
    }
    @Test
    public void testDataReader(){
        UsTreasuryDataReader usTreasuryDataReader = new UsTreasuryDataReader();
        List<LocalDate> dates = usTreasuryDataReader.getDates(DOCUMENT);
        NavigableMap<Integer, Double> dateSpecificParData = usTreasuryDataReader.getsParDataForDate(dates.get(1), DOCUMENT);
        assertEquals( 8, dateSpecificParData.size());
    }

    @Test
    public void testDataReaderToZero(){
        double precision = 0.000001;
        Interpolator interpolator = new LogarithmicInterpolator();
        LocalDate testDate = new UsTreasuryDataReader().getDates(DOCUMENT).getFirst();
        NavigableMap<Integer, Double> dateSpecificParData = new UsTreasuryDataReader().getsParDataForDate(testDate, DOCUMENT);
        Curves curves = Curves.of(precision, interpolator, dateSpecificParData);
        NavigableMap<Integer, Double> zeroCurve = curves.getZeroCurve().getMonthToRateCurve();
        NavigableMap<Integer, Double> expectedZeroCurve = new TreeMap<>();
        expectedZeroCurve.put(6, 0.048255956591810145);
        assertEquals( expectedZeroCurve.get(6), zeroCurve.get(6));
    }

    public static Document getDocument(String doc) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;

        try {
            docBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Document parse;

        try {
            parse = docBuilder.parse("src/datareader/test/"+doc);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        return parse;
    }

}

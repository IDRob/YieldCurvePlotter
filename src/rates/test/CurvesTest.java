package rates.test;

import rates.Curves;
import numericalsolutions.interpolator.Interpolator;
import numericalsolutions.interpolator.LogarithmicInterpolator;
import org.junit.Test;

import java.util.NavigableMap;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

/**
 * Test class for Curves.
 */
public class CurvesTest {

    /**
     * The precision for monthly zero rate.
     */
    private final double PRECISION = 0.0000001;

    /**
     * The interpolator used to estimate monthly rates.
     */
    private final Interpolator INTERPOLATOR = new LogarithmicInterpolator();
    @Test
    public void testZeroMonthlyCurveFromPar() {
        NavigableMap<Integer, Double> parRates = new TreeMap<>();
        parRates.put(12, 0.01530);
        parRates.put(24, 0.01520);
        parRates.put(60, 0.01560);
        NavigableMap<Integer, Double> zeroCurve = Curves.zeroMonthlyCurveFromPar(parRates, PRECISION);

        NavigableMap<Integer, Double> expectedZeroRates = new TreeMap<>();
        expectedZeroRates.put(12, 0.0012711);
        expectedZeroRates.put(24, 0.0012628);
        expectedZeroRates.put(60, 0.0012962);
        assertEquals(expectedZeroRates.get(12), zeroCurve.get(12), PRECISION);
        assertEquals(expectedZeroRates.get(24), zeroCurve.get(24), PRECISION);
        assertEquals(expectedZeroRates.get(60), zeroCurve.get(60), PRECISION);
    }

    @Test
    public void testBootstrapperToYearlyZeroCurvePerMonth() {
        NavigableMap<Integer, Double> parRates = new TreeMap<>();
        parRates.put(12, 0.01530);
        parRates.put(24, 0.01520);
        parRates.put(60, 0.01560);
        Curves curves = Curves.of(PRECISION, INTERPOLATOR, parRates);
        NavigableMap<Integer, Double> zeroCurve = curves.getZeroCurve().getMonthToRateCurve();

        NavigableMap<Integer, Double> expectedZeroRates = new TreeMap<>();
        expectedZeroRates.put(12, 0.0153599);
        expectedZeroRates.put(13, 0.0153443);
        expectedZeroRates.put(24, 0.0152587);
        expectedZeroRates.put(29, 0.01537556);
        expectedZeroRates.put(46, 0.01558288);
        expectedZeroRates.put(60, 0.01566541);

        assertEquals(expectedZeroRates.get(12), zeroCurve.get(12), PRECISION);
        assertEquals(expectedZeroRates.get(13), zeroCurve.get(13), PRECISION);
        assertEquals(expectedZeroRates.get(24), zeroCurve.get(24), PRECISION);
        assertEquals(expectedZeroRates.get(29), zeroCurve.get(29), PRECISION);
        assertEquals(expectedZeroRates.get(46), zeroCurve.get(46), PRECISION);
        assertEquals(expectedZeroRates.get(60), zeroCurve.get(60), PRECISION);
    }

    @Test
    public void testBootstrapperSixMonthsToOneYear() {
        NavigableMap<Integer, Double> parRates = new TreeMap<>();
        parRates.put(6, 0.01530);
        parRates.put(12, 0.01520);
        Curves curves = Curves.of(PRECISION, INTERPOLATOR, parRates);
        NavigableMap<Integer, Double> zeroCurve = curves.getZeroCurve().getMonthToRateCurve();

        NavigableMap<Integer, Double> expectedZeroRates = new TreeMap<>();
        expectedZeroRates.put(6, 0.0153599);
        expectedZeroRates.put(12, 0.0152589);

        assertEquals(expectedZeroRates.get(6), zeroCurve.get(6), PRECISION);
        assertEquals(expectedZeroRates.get(12), zeroCurve.get(12), PRECISION);
    }
}

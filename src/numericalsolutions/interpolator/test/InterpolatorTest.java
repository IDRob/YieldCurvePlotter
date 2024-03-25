package numericalsolutions.interpolator.test;

import numericalsolutions.interpolator.Interpolator;
import numericalsolutions.interpolator.LogarithmicInterpolator;
import org.junit.Test;

import java.util.NavigableMap;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

/**
 * The test class for Interpolator.
 */
public class InterpolatorTest {

    /**
     * The monthly zero rate precision.
     */
    private final double PRECISION = 0.000001;
    @Test
    public void testInterpolator() {
        NavigableMap<Integer, Double> discountFactorCurve = new TreeMap<>();
        discountFactorCurve.put(12,  0.98488);
        discountFactorCurve.put(24,  0.97017);
        discountFactorCurve.put(60,  0.92524);
        Interpolator interpolator = new LogarithmicInterpolator();
        double interpolateValue = interpolator.interpolate(13, discountFactorCurve);
        assertEquals( 0.983646, interpolateValue, PRECISION);
    }
}

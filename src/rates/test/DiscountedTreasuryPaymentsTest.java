package rates.test;

import rates.DiscountedTreasuryPayments;
import org.junit.Test;

import java.util.NavigableMap;
import java.util.TreeMap;

import static java.lang.Math.pow;
import static org.junit.Assert.assertEquals;

/**
 * Test class for DiscountedTreasuryPayments.
 */
public class DiscountedTreasuryPaymentsTest {

    @Test
    public void testDiscountPayments() {
        int months = 24;
        double parRate = 0.0152;
        NavigableMap<Integer, Double> existingZeroRates = new TreeMap<>();
        existingZeroRates.put(12, 0.00127042870969033);
        DiscountedTreasuryPayments discountedTreasuryPayments = new DiscountedTreasuryPayments(months, parRate, existingZeroRates);
        double result = discountedTreasuryPayments.runsFunction(0.0012624288284146);
        assertEquals( 0.0, result, 0.00001);
    }

    @Test
    public void testSixMonthPayment() {
        int months = 6;
        double parRate = 0.0152;
        double zeroRate = pow(1 + parRate, 1d / 12) - 1;
        NavigableMap<Integer, Double> existingZeroRates = new TreeMap<>();
        DiscountedTreasuryPayments discountedTreasuryPayments = new DiscountedTreasuryPayments(months, parRate, existingZeroRates);
        double result = discountedTreasuryPayments.runsFunction(zeroRate);
        assertEquals( 0.0000, result, 0.00001);
    }
}

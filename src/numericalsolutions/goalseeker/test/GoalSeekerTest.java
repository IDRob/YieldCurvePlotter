package numericalsolutions.goalseeker.test;

import rates.DiscountedTreasuryPayments;
import numericalsolutions.goalseeker.GoalSeeker;
import org.junit.Test;

import java.util.NavigableMap;
import java.util.TreeMap;

import static java.lang.Math.pow;
import static org.junit.Assert.assertEquals;

/**
 * The test class for the Goal Seeker Class.
 */
public class GoalSeekerTest {

    /**
     * The monthly zero rate precision.
     */
    private final double PRECISION = 0.000001;

    @Test
    public void testGoalSeeker1Yr(){
        int months = 12;
        double parRate = 0.0153;
        NavigableMap<Integer, Double> existingZeroRates = new TreeMap<>();
        DiscountedTreasuryPayments discountedTreasuryPayments = new DiscountedTreasuryPayments(months, parRate, existingZeroRates);
        GoalSeeker goalSeeker = new GoalSeeker(discountedTreasuryPayments);
        double result = goalSeeker.secantMethod( 0, PRECISION);
        assertEquals( 0.001272, result, PRECISION);
    }

    @Test
    public void testGoalSeeker2Yr(){
        int months = 24;
        double parRate = 0.0152;
        NavigableMap<Integer, Double> existingZeroRates = new TreeMap<>();
        existingZeroRates.put(12, 0.00127);
        DiscountedTreasuryPayments discountedTreasuryPayments = new DiscountedTreasuryPayments(months, parRate, existingZeroRates);
        GoalSeeker goalSeeker = new GoalSeeker(discountedTreasuryPayments);
        double result = goalSeeker.secantMethod( 0, PRECISION);
        assertEquals( 0.001264, result, PRECISION);
    }

    @Test
    public void testGoalSeeker5Yr(){
        int months = 60;
        double parRate = 0.0156;
        NavigableMap<Integer, Double> existingZeroRates = new TreeMap<>();
        existingZeroRates.put(12, 0.001272);
        existingZeroRates.put(24, 0.001264);
        DiscountedTreasuryPayments discountedTreasuryPayments = new DiscountedTreasuryPayments(months, parRate, existingZeroRates);
        GoalSeeker goalSeeker = new GoalSeeker(discountedTreasuryPayments);
        double result = goalSeeker.secantMethod( 0, PRECISION);
        assertEquals( 0.001298, result, PRECISION);
    }

    @Test
    public void testGoalSeeker6Months() {
        int months = 6;
        double parRate = 0.0152;
        double expectedZeroRate = pow(1 + parRate / 2, 1d / 6) - 1;
        NavigableMap<Integer, Double> existingZeroRates = new TreeMap<>();
        DiscountedTreasuryPayments discountedTreasuryPayments = new DiscountedTreasuryPayments(months, parRate, existingZeroRates);
        GoalSeeker goalSeeker = new GoalSeeker(discountedTreasuryPayments);
        double result = goalSeeker.secantMethod( 0, PRECISION);
        assertEquals(expectedZeroRate, result, PRECISION * 2);
    }


}

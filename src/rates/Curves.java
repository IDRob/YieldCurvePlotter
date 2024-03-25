package rates;

import numericalsolutions.goalseeker.GoalSeeker;
import numericalsolutions.interpolator.Interpolator;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.lang.Math.pow;

/**
 * This class produces and holds equivalent yield curves.
 */
public class Curves {

    /**
     * The par yield curve.
     */
    private final Curve parCurve;

    /**
     * The zero yield curve.
     */
    private final Curve zeroCurve;

    /**
     * The discount factor curve.
     */
    private final Curve discountFactorCurve;

    private Curves(
            Curve parCurve,
            Curve zeroCurve,
            Curve discountFactorCurve) {

        this.parCurve = parCurve;
        this.zeroCurve = zeroCurve;
        this.discountFactorCurve = discountFactorCurve;
    }

    /**
     * Initializes an instance of Curves which holds the par curve, the zero curve and the discount factor curve.
     * <p>
     * The zero curve and the discount factor curve are bootstrapped from the par curve.
     *
     * @param precision the precision of the zero curve rates
     * @param interpolator the interpolator used to estimate missing values within the curve
     * @param parCurve the par curve
     * @return the Curves instance
     */
    public static Curves of(double precision, Interpolator interpolator, NavigableMap<Integer, Double> parCurve) {
        NavigableMap<Integer, Double> monthlyZeroCurve = zeroMonthlyCurveFromPar(parCurve, precision);
        NavigableMap<Integer, Double> zeroCurve = convertMonthlyYieldToYearly(monthlyZeroCurve);
        NavigableMap<Integer, Double> discountFactorCurve = convertZeroToDiscountFactor(zeroCurve);
        NavigableMap<Integer, Double> interpolatedDiscountFactorCurve = interpolateCurveValues(discountFactorCurve, interpolator);
        NavigableMap<Integer, Double> interpolatedZeroCurve = convertDiscountFactorToZero(interpolatedDiscountFactorCurve);

        return new Curves(
                Curve.of(parCurve),
                Curve.of(interpolatedZeroCurve),
                Curve.of(interpolatedDiscountFactorCurve));
    }

    /**
     * Gets the par curve.
     *
     * @return the par curve
     */
    public Curve getParCurve() {
        return parCurve;
    }

    /**
     * Gets the zero curve.
     *
     * @return the zero curve
     */
    public Curve getZeroCurve() {
        return zeroCurve;
    }


    /**
     * Bootstraps the monthly rates of the zero curve from the given par curve.
     * <p>
     * Visible for testing.
     *
     * @param parCurve the par curve
     * @param precision the zero curve precision
     * @return the monthly zero curve
     */
    public static NavigableMap<Integer, Double> zeroMonthlyCurveFromPar(
            NavigableMap<Integer, Double> parCurve,
            double precision) {

        NavigableMap<Integer, Double> zeroRates = new TreeMap<>();
        for(Map.Entry<Integer, Double> entry: parCurve.entrySet()) {
            int month = entry.getKey();
            double parRate = entry.getValue();
            DiscountedTreasuryPayments discountedTreasuryPayments = new DiscountedTreasuryPayments(month, parRate, zeroRates);
            GoalSeeker goalSeeker = new GoalSeeker(discountedTreasuryPayments);
            double zeroRate = goalSeeker.secantMethod(0d, precision);
            zeroRates.put(month, zeroRate);
        }

        return zeroRates;
    }

    private static NavigableMap<Integer, Double> convertMonthlyYieldToYearly(NavigableMap<Integer, Double> yieldCurve) {
        return new TreeMap<>(yieldCurve.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> pow(e.getValue() +1, 12)-1)));
    }

    private static NavigableMap<Integer, Double> convertZeroToDiscountFactor(NavigableMap<Integer, Double> yieldCurve) {
        return new TreeMap<>(yieldCurve.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> pow(1/(e.getValue()+1), (double) e.getKey() / 12))));
    }

    private static NavigableMap<Integer, Double> convertDiscountFactorToZero(NavigableMap<Integer, Double> yieldCurve) {
        return new TreeMap<>(yieldCurve.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> pow(1/(e.getValue()), 12/(double) e.getKey()) - 1)));
    }

    private static NavigableMap<Integer, Double> interpolateCurveValues(
            NavigableMap<Integer, Double> yieldCurve,
            Interpolator interpolator) {

        NavigableMap<Integer, Double> discountFactorRates = new TreeMap<>();
        for (int i = yieldCurve.firstKey(); i <= yieldCurve.lastKey(); i++) {
            discountFactorRates.put(i, interpolator.interpolate(i, yieldCurve));
        }
        return  discountFactorRates;
    }

}

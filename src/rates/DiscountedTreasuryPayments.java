package rates;

import numericalsolutions.goalseeker.Function;

import java.util.NavigableMap;

import static java.lang.Math.pow;

/**
 * This class finds the discounted present value of treasury bill cash flows.
 */
public class DiscountedTreasuryPayments implements Function {

    /**
     * The months remaining on the treasury bill.
     */
    private final int months;

    /**
     * The par rate of the treasury bill.
     */
    private final double parRate;

    /**
     * The known zero rates for given months.
     */
    private final NavigableMap<Integer, Double> monthToZeroRates;

    /**
     * Instantiates the class.
     *
     * @param months as int
     * @param parRate as double
     * @param monthToZeroRates as Navigable Map<Integer, Double>
     */
    public DiscountedTreasuryPayments(int months, double parRate, NavigableMap<Integer, Double> monthToZeroRates) {
        this.months = months;
        this.parRate = parRate;
        this.monthToZeroRates = monthToZeroRates;
    }

    @Override
    public double runsFunction(double zeroRate) {
        double coupon = parRate / 2.0;

        double discountedSumOfPayments = 0.0;
        for(int i = 6; i < months;) {
            double applicableZeroRate = getZeroRateForMonth(i, zeroRate);

            double discountFactor = pow(1 + applicableZeroRate, i);
            double discountedPayment = coupon / discountFactor;
            discountedSumOfPayments += discountedPayment;
            i += 6;
        }
        double discountFactor = pow(1 + zeroRate, months);
        double discountedFinalPayment = (1 + coupon) / discountFactor;
        discountedSumOfPayments += discountedFinalPayment;
        return discountedSumOfPayments - 1;
    }

    private double getZeroRateForMonth(int month, double newZeroRate) {
        Integer key = monthToZeroRates.ceilingKey(month);
        if (key != null) {
            return monthToZeroRates.get(key);
        } else {
            return newZeroRate;
        }
    }
}

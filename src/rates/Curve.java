package rates;

import java.util.NavigableMap;

/**
 * This class holds month and rate information for a yield curve.
 */
public class Curve {

    /**
     * The month to rate yield curve map.
     */
    private final NavigableMap<Integer, Double> curve;

    private Curve(NavigableMap<Integer, Double> curve) {
        this.curve = curve;
    }

    /**
     * Initializes a curve.
     *
     * @param curveMap the
     * @return the Curve
     */
    public static Curve of(NavigableMap<Integer, Double> curveMap) {
        return new Curve(curveMap);
    }

    /**
     * A map from integer month to yield rate.
     * <p>
     * E.g. key: 1, value: 0.05, means that the one-month yield rate is 5%.
     *
     * @return the month to rate map
     */
    public NavigableMap<Integer, Double> getMonthToRateCurve() {
        return curve;
    }
}

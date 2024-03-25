package numericalsolutions.interpolator;

import java.util.NavigableMap;

/**
 * This class interpolates values for a given navigable map of integer keys and double values.
 */
public interface Interpolator {

    /**
     * This method interpolates a value for a given key within the given data points.
     *
     * @param key the key to interpolate a value
     * @param dataPoints the data points to interpolate from
     * @return interpolated value for given key
     */
    double interpolate(int key, NavigableMap<Integer, Double> dataPoints);
}

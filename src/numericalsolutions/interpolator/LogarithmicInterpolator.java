package numericalsolutions.interpolator;

import java.util.Map;
import java.util.NavigableMap;

import static java.lang.Math.exp;
import static java.lang.Math.log;

/**
 * This class interpolates a value using a logarithmic scale.
 */
public class LogarithmicInterpolator implements Interpolator {


    @Override
    public double interpolate(int key, NavigableMap<Integer, Double> dataPoints){
        Map.Entry<Integer, Double> floorEntry = dataPoints.floorEntry(key);
        Map.Entry<Integer, Double> ceilingEntry = dataPoints.ceilingEntry(key);

        if ((floorEntry == null) || (ceilingEntry == null) ) {
            throw new IllegalArgumentException("Cannot interpolate value for key that is outside of datapoint keys");
        }
        if (floorEntry.equals(ceilingEntry)) {
            return floorEntry.getValue();
        }


        double lnFloor = log(floorEntry.getValue());
        double lnCeiling = log(ceilingEntry.getValue());

        double slope = (lnCeiling - lnFloor) / (ceilingEntry.getKey() - floorEntry.getKey()) ;
        double interpolatedLnValue = lnFloor + slope * (key - floorEntry.getKey());
        return exp(interpolatedLnValue);
    }
}

package numericalsolutions.goalseeker;

import static java.lang.Math.pow;

/**
 * This class seeks the input value for a given function to output a given goal.
 */
public class GoalSeeker {

    /**
     * The function used in the seeker
     */
    private final Function function;

    /**
     * Initializes the GoalSeeker class.
     *
     * @param function as a Function
     */
    public GoalSeeker(Function function) {
        this.function = function;

    }

    /**
     * Gets the x value such that Function(x) is approximately equal to the goal.
     * <p>
     * An iterative Secant process is used to find the x value which minimises the square of the difference
     * between F(x) and the goal to a given precision of x.
     * Source 1: <a href="https://www.baeldung.com/java-gradient-descent#:~:text=Gradient%20Descent%20is%
     * 20an%20optimization,and%20descent%20means%20going%20down">...</a>.
     * Source 2: <a href="https://www.geeksforgeeks.org/secant-method-of-numerical-analysis/">...</a>
     *
     * @param goal the output goal of the function
     * @param precision the precision of the x variable
     * @return the sought x value
     */
    public double secantMethod(double goal, double precision) {

        double previousX = 0.001; // initial estimate of x
        double previousY =  pow(function.runsFunction(previousX) - goal, 2);
        double previousStep = 0.0005; // initial x step
        double currentX = previousX + previousStep;
        int iter = 100;
        while (previousStep > precision && iter > 0) {
            iter--;
            double currentY = pow(function.runsFunction(currentX) - goal, 2);

            double multiplier = secantMultiplier(currentX, previousX, currentY, previousY);

            previousX = currentX;
            currentX -= multiplier * currentY;
            previousY = currentY;
            previousStep = StrictMath.abs(currentX - previousX);
        }

        return currentX;
    }

    private double secantMultiplier(double currentX, double previousX, double currentY, double previousY) {
        if (currentY == previousY) {
            return 0d;
        }
        return  (previousX - currentX) / (previousY - currentY);
    }
}

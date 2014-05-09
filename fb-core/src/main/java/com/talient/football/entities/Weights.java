// CVS ID: $Header: /talient/cvsroot2/fb-core/src/main/java/com/talient/football/entities/Weights.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $

package com.talient.football.entities;

import java.text.NumberFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Properties;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public class Weights {

    private Weights() {};

    private static final int[] WEIGHTS12 =
        {135, 121, 110, 100, 91, 83, 75, 68, 62, 57, 51, 47};

    private static final int[] WEIGHTS13 =
        {129, 116, 106, 96, 87, 79, 72, 66, 60, 54, 49, 45, 41};

    private static final int[] WEIGHTS14 =
        {123, 112, 102, 93, 84, 77, 70, 63, 58, 52, 48, 43, 39, 36};

    private static final int[] WEIGHTS15 =
        {119, 109, 99, 90, 82, 74, 67, 61, 56, 51, 46, 42, 38, 35, 31};

    private static final int[] WEIGHTS18 =
        {11083, 10077, 9161, 8328, 7571, 6883, 6257, 5688, 5171, 4701, 4274, 3885, 3532, 3211, 2919, 2654, 2412, 2193};

    private static final int[] WEIGHTS25 =
        {25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

    public static final int[][] WEIGHTS =
        {null, null, null, null, null, 
         null, null, null, null, null,
         null, WEIGHTS12, WEIGHTS13, WEIGHTS14, WEIGHTS15,
         null, null, WEIGHTS18, null, null,
         null, null, null, null, WEIGHTS25};

    private static int total = 1000;

    private static double factor = 1.1;

    public static int[] getWeights(int total, double factor, int games) {

        double factorSum = 0.0;
        double currentFactor = 1.0;

        for (int i = 0; i < games; i++) {
            factorSum += currentFactor;
            currentFactor *= factor;
        }

        int[] weights = new int[games];

        double currentWeight = total / factorSum;
        int weightSum = 0;

        for (int i = games - 1; i >= 0; i--) {
            weights[i] = (int)Math.round(currentWeight);
            weightSum += weights[i];
            currentWeight *= factor;
        }

        // Correct for accumulated rounding errors.
        weights[0] += total - weightSum;

        return weights;
    }

    public static NumberFormat getNumberFormat() {
        final String poolType =
            System.getProperties().getProperty("football.pooltype", "");
        if (poolType.equals("cfpool")) {
            return new CfpoolFormat();
        } else if (poolType.equals("nflpool")) {
            return NumberFormat.getInstance();
        } else {
            throw new RuntimeException(
                "Could not determine football.pooltype (" + poolType + ")");
        }
    }

    public static int[] getWeights(int games) {
        if (games > 17) {
            return getWeights(100000, Weights.factor, games);
        }
        else {
            return getWeights(Weights.total, Weights.factor, games);
        }
    }

    public static String toString(int[] weights, int pos) {
        if (weights.length > 17) {
            return Float.toString(Float.valueOf(Integer.toString(weights[pos])).floatValue() / 100);
        }
        else {
            return Integer.toString(weights[pos]);
        }
    }

    private static class CfpoolFormat extends NumberFormat {

        private NumberFormat target = NumberFormat.getInstance();

        public StringBuffer format(double number,
                                   StringBuffer toAppendTo,
                                   FieldPosition pos) {
            return target.format(number/100, toAppendTo, pos);
        }

        public StringBuffer format(long number,
                                   StringBuffer toAppendTo,
                                   FieldPosition pos) {
            return target.format((double)number/100, toAppendTo, pos);
        }

        public Number parse(String text,
                            ParsePosition parsePosition) {
            Number number = target.parse(text, parsePosition);
            return new Double(number.doubleValue()*100);
        }

        public int getMaximumFractionDigits() {
            return target.getMaximumFractionDigits();
        }

        public int getMinimumFractionDigits() {
            return target.getMinimumFractionDigits();
        }

        public int getMaximumIntegerDigits() {
            return target.getMaximumIntegerDigits();
        }

        public int getMinimumIntegerDigits() {
            return target.getMinimumIntegerDigits();
        }

        public boolean isGroupingUsed() {
            return target.isGroupingUsed();
        }

        public boolean isParseIntegerOnly() {
            return target.isParseIntegerOnly();
        }

        public void setGroupingUsed(boolean newValue) {
            target.setGroupingUsed(newValue);
        }

        public void setParseIntegerOnly(boolean newValue) {
            target.setParseIntegerOnly(newValue);
        }

        public void setMaximumFractionDigits(int newValue) {
            target.setMaximumFractionDigits(newValue);
        }

        public void setMinimumFractionDigits(int newValue) {
            target.setMinimumFractionDigits(newValue);
        }

        public void setMaximumIntegerDigits(int newValue) {
            target.setMaximumIntegerDigits(newValue);
        }

        public void setMinimumIntegerDigits(int newValue) {
            target.setMinimumIntegerDigits(newValue);
        }
    }

    private static NumberFormat format = null;

    public static void main(String argv[]) {
        Properties p = System.getProperties();

        p.setProperty("football.pooltype", "nflpool"); 
        System.setProperties(p);

        NumberFormat f = getNumberFormat();

        int[] weights = getWeights(12);
        for (int i = 0; i < weights.length; i++) {
            System.out.println(f.format(weights[i]));
        }
        weights = getWeights(13);
        for (int i = 0; i < weights.length; i++) {
            System.out.println(f.format(weights[i]));
        }
        weights = getWeights(14);
        for (int i = 0; i < weights.length; i++) {
            System.out.println(f.format(weights[i]));
        }
        weights = getWeights(15);
        for (int i = 0; i < weights.length; i++) {
            System.out.println(f.format(weights[i]));
        }

        p.setProperty("football.pooltype", "cfpool"); 
        System.setProperties(p);

        f = getNumberFormat();

        weights = getWeights(18);
        for (int i = 0; i < weights.length; i++) {
            System.out.println(f.format(weights[i]));
        }
    }
}

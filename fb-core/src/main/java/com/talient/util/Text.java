// CVS ID: @(#) $Id: Text.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $

package com.talient.util;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Utility methods for manipulating text, including padding strings
 * to certain widths (or trimming, if necessary) and creating a set
 * of columns from a list.  All of these methods assume a fixed-width
 * font.
 */
public final class Text {

    private Text() {}


    /**
     * Return a string padded with spaces, if necessary, to fill
     * the given number of columns.  The given string will be
     * right-justified within the returned string.  If the given
     * string is longer than the specified length, the whole
     * string will be returned.  It will not be trimmed.
     *
     * @param string The string to be padded
     * @param cols The desired minimum length of the returned string
     * @return padded string
     */
    public static String padRight(String string, int cols) {
        return padRight(string, cols, false);
    }


    /**
     * Return a string padded with spaces, if necessary, to fill
     * the given number of columns.  The given string will be
     * right-justified within the returned string.  If the given
     * string is longer than the specified length, the string will
     * be trimmed if requested.  If the string is truncated, the
     * returned string will be from the end of the given string.
     *
     * @param string The string to be padded
     * @param cols The desired minimum length of the returned string
     * @param trim true if the string should be truncated to the requested
     *             length if necessary
     * @return padded (or truncated) string
     */
    public static String padRight(String string, int cols, boolean trim) {
        final String result;

        if (string.length() > cols) {
            if (trim) {
                result = string.substring(string.length() - cols,
                                          string.length());
            } else {
                result = string;
            }
        } else if (string.length() == cols) {
            result = string;
        } else {
            final StringBuffer b = new StringBuffer(cols);
            for (int i = cols - string.length(); i > 0; i--) {
                b.append(" ");
            }
            b.append(string);
            result = b.toString();
        }
        return result;
    }


    /**
     * Return a string padded with spaces, if necessary, to fill
     * the given number of columns.  The given string will be
     * left-justified within the returned string.  If the given
     * string is longer than the specified length, the whole
     * string will be returned.  It will not be trimmed.
     *
     * @param string The string to be padded
     * @param cols The desired minimum length of the returned string
     * @return padded string
     */
    public static String padLeft(String string, int cols) {
        return padLeft(string, cols, false);
    }


    /**
     * Return a string padded with spaces, if necessary, to fill
     * the given number of columns.  The given string will be
     * left-justified within the returned string.  If the given
     * string is longer than the specified length, the string will
     * be trimmed if requested.  If the string is truncated, the
     * returned string will be from the beginning of the given string.
     *
     * @param string The string to be padded
     * @param cols The desired minimum length of the returned string
     * @param trim true if the string should be truncated to the requested
     *             length if necessary
     * @return padded (or truncated) string
     */
    public static String padLeft(String string, int cols, boolean trim) {
        final String result;

        if (string.length() > cols) {
            if (trim) {
                result = string.substring(0, cols);
            } else {
                result = string;
            }
        } else if (string.length() == cols) {
            result = string;
        } else {
            final StringBuffer b = new StringBuffer(cols);
            b.append(string);
            for (int i = cols - string.length(); i > 0; i--) {
                b.append(" ");
            }
            result = b.toString();
        }
        return result;
    }

    /**
     * Make a set of columns from the given list.  Each item in the
     * returned list is a String containing one row from the
     * resulting columnar-display.  The rows are built by appending
     * the appropriate strings from the given list.  It is up to the
     * calling function to make certain the values will align properly
     * when concatenated together (i.e. use the padXXX methods).
     *
     * Also note that this implementation will access every item in the
     * given list in a non-sequencial order.  Therefore, it is best to
     * pass in a List implementation that has optimal performance for
     * random-access gets (e.g. ArrayList).
     *
     * @param strings list of strings to turn into columns
     * @param cols number of columns to create
     * @return the rows in the columnar-display
     */
    public static List makeColumns(List strings, int cols) {

        if (cols <= 0) {
            throw new IllegalArgumentException(
                "Number of columns must be greater than zero (" + cols + ")");
        }

        // Minimum height of a column.
        final int minHeight = strings.size() / cols;

        // Number of columns with extra entries.
        final int extras = strings.size() % cols;

        // Maximum height of a column.
        final int maxHeight = minHeight + (extras != 0 ? 1 : 0);

        // Determine the index of the top item in each column.
        final int[] tops = new int[cols];

        // We start with zero (redundant, I know)...
        tops[0] = 0;

        // ... and calculate the rest.
        for (int i = 1; i < cols; i++) {
            if (i <= extras) {
                tops[i] = tops[i-1] + maxHeight;
            } else {
                tops[i] = tops[i-1] + minHeight;
            }
        }

        final List rows = new ArrayList(maxHeight);
        for (int i = 0; i < maxHeight; i++) {
            final StringBuffer b = new StringBuffer(80);
            for (int j = 0; j < cols; j++) {
                if (i < minHeight || j < extras) {
                    b.append(String.valueOf(strings.get(tops[j] + i)));
                }
            }
            rows.add(b.toString());
        }
        return rows;
    }

    public static void main(String argv[]) {
        final String testString = "testString";

        System.out.println("testString = \"" +
            testString + "\"\n");

        System.out.println("padLeft(testString, 16) = \"" +
            padLeft(testString, 16) + "\"\n");

        System.out.println("padRight(testString, 16) = \"" +
            padRight(testString, 16) + "\"\n");

        System.out.println("padLeft(testString, 4, true) = \"" +
            padLeft(testString, 4, true) + "\"\n");

        System.out.println("padRight(testString, 4, true) = \"" +
            padRight(testString, 4, true) + "\"\n");

        System.out.println("padLeft(testString, 4, false) = \"" +
            padLeft(testString, 4, false) + "\"\n");

        System.out.println("padRight(testString, 4, false) = \"" +
            padRight(testString, 4, false) + "\"\n");

        System.out.println("padLeft(testString, 10, true) = \"" +
            padLeft(testString, 10, true) + "\"\n");

        System.out.println("padRight(testString, 10, true) = \"" +
            padRight(testString, 10, true) + "\"\n");

        System.out.println("padLeft(testString, 10, false) = \"" +
            padLeft(testString, 10, false) + "\"\n");

        System.out.println("padRight(testString, 10, false) = \"" +
            padRight(testString, 10, false) + "\"\n");

        System.out.println("padLeft(testString, 16, true) = \"" +
            padLeft(testString, 16, true) + "\"\n");

        System.out.println("padRight(testString, 16, true) = \"" +
            padRight(testString, 16, true) + "\"\n");

        System.out.println("padLeft(testString, 16, false) = \"" +
            padLeft(testString, 16, false) + "\"\n");

        System.out.println("padRight(testString, 16, false) = \"" +
            padRight(testString, 16, false) + "\"\n");

        final String[] nineStrings =
           {"One   ", "Two   ", "Three ", "Four  ", "Five  ",
            "Six   ", "Seven ", "Eight ", "Nine  " };

        final String[] tenStrings =
           {"One   ", "Two   ", "Three ", "Four  ", "Five  ",
            "Six   ", "Seven ", "Eight ", "Nine  ", "Ten   " };

        final String[] elevenStrings =
           {"One    ", "Two    ", "Three  ", "Four   ", "Five   ",
            "Six    ", "Seven  ", "Eight  ", "Nine   ", "Ten    ", "Eleven " };

        System.out.println("Nine Strings, Three columns...");
        for (Iterator i = makeColumns(Arrays.asList(nineStrings), 3).iterator();
             i.hasNext(); ) {
            System.out.println(i.next());
        }

        System.out.println("\nTen Strings, Three columns...");
        for (Iterator i = makeColumns(Arrays.asList(tenStrings), 3).iterator();
             i.hasNext(); ) {
            System.out.println(i.next());
        }

        System.out.println("\nEleven Strings, Three columns...");
        for (Iterator i =
                 makeColumns(Arrays.asList(elevenStrings), 3).iterator();
             i.hasNext(); ) {
            System.out.println(i.next());
        }

        System.out.println("\nTen Strings, Four columns...");
        for (Iterator i = makeColumns(Arrays.asList(tenStrings), 4).iterator();
             i.hasNext(); ) {
            System.out.println(i.next());
        }
    }
}

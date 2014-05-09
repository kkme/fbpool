// CVS ID: @(#) $Id: MissingResourceException.java,v 1.1.1.1 2005-08-25 18:13:09 husker Exp $

package com.talient.util;

/**
 * <p>
 * @author Steve Schmadeke
 * @version $Version: $
 */
public class MissingResourceException extends Exception {

    public MissingResourceException() {
        super();
    }

    public MissingResourceException(String text) {
        super(text);
    }
}

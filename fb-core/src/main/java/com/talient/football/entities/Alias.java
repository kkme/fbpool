// SCCS ID: @(#) 08/23/98 1.1 Alias.java

package com.talient.football.entities;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Date;

/**
 * <p>
 * @author Steve Schmadeke
 * @version 1.1
 */
public class Alias {

    private Entrant entrant;
    private String username;
    private String password;
    private Date lastLog;

    public Alias(Entrant entrant, String username, String password) {
        this.entrant = entrant;
        this.username = username;
        this.password = password;
    }
    
    public void    setEntrant(Entrant t)       { entrant = t; }
    public Entrant getEntrant()                { return entrant; }
    public void    setUsername(String name)    { username = name; }
    public String  getUsername()               { return username; }
    public void    setPassword(String name)    { password = name; }
    public String  getPassword()               { return password; }
    public void    setLastLog(Date last)       { lastLog = last; }
    public Date    getLastLog()                { return lastLog; }
    
    public String toString() { return this.username; }

    public boolean equals(Object other) {
        if (other instanceof Team) {
            return getUsername().equals(((Alias)other).getUsername());
        }
        return false;
    }

    public static void main(String[] args) {
        Entrant e = new Entrant("Nick Lonsky", "nick@lonsky.com");
        Alias entrant = new Alias(e, "nlonsky", "Dec5");

        System.out.println("Username: " + entrant.getUsername());
        System.out.println("Password: " + entrant.getPassword());
    }
}

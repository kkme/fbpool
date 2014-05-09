// CVS ID: @(#) $Id: TextEntry.java,v 1.3 2008-11-16 22:58:27 husker Exp $

package com.talient.football.view.text;

import java.text.SimpleDateFormat;
import java.text.DecimalFormat;

import java.util.List;
import java.util.Iterator;

import com.talient.util.Properties;
import com.talient.util.MissingPropertyException;

import com.talient.football.entities.Game;
import com.talient.football.entities.WeeklySchedule;

public class TextEntry {

    private final WeeklySchedule weeklySchedule;

    private final String name =
        Properties.getProperty("football.pool.name");

    private final String email =
        Properties.getProperty("football.pool.email");

    private final String url =
        Properties.getProperty("football.pool.url");

    public TextEntry(WeeklySchedule weeklySchedule) {
        this.weeklySchedule = weeklySchedule;
    }

    public String formatHeader() throws MissingPropertyException {

        if (name == null) {
            throw new MissingPropertyException(
                "The football.pool.name property has not been set");
        }

        if (email == null) {
            throw new MissingPropertyException(
                "The football.pool.email property has not been set");
        }

        final StringBuffer buffer = new StringBuffer(160);

        buffer.append("From: ");
        buffer.append(name);
        buffer.append(" <");
        buffer.append(email);
        buffer.append(">\n");
        buffer.append("Subject: ");
        buffer.append(name);
        buffer.append(" ");
        buffer.append(weeklySchedule.getLabel());
        buffer.append(" Entry Form\n");

        return buffer.toString();
    }

    public String formatReminderHeader() throws MissingPropertyException {

        if (name == null) {
            throw new MissingPropertyException(
                "The football.pool.name property has not been set");
        }

        if (email == null) {
            throw new MissingPropertyException(
                "The football.pool.email property has not been set");
        }

        final StringBuffer buffer = new StringBuffer(512);

        buffer.append("From: ");
        buffer.append(name);
        buffer.append(" <");
        buffer.append(email);
        buffer.append(">\n");
        buffer.append("Subject: ");
        buffer.append(name);
        buffer.append(" ");
        buffer.append(weeklySchedule.getLabel());
        buffer.append(" Reminder\n\n");

        buffer.append("This is an automated reminder to submit your picks\n");
        buffer.append("for the ");
        buffer.append(name);
        buffer.append(" by ");
        buffer.append(reminderDateFormat.format(weeklySchedule.getStartTime()));
        buffer.append(".  We have \n");
        buffer.append("not yet received your picks for this week.\n");

        return buffer.toString();
    }

    public String formatEntry() throws MissingPropertyException {

        if (name == null) {
            throw new MissingPropertyException(
                "The football.pool.name property has not been set");
        }

        if (url == null) {
            throw new MissingPropertyException(
                "The football.pool.url property has not been set");
        }

        final String spacer = weeklySchedule.isFinal() ? " vs " : " at ";

        final StringBuffer buffer = new StringBuffer(1024);

        buffer.append("\nTo enter using your browser (preferred), go to:");
        buffer.append("\n\n    ");
        buffer.append(url);
        buffer.append("fb-web/EntryForm.jsp?year=");
        buffer.append(weeklySchedule.getYear());
        buffer.append("&week=");
        buffer.append(weeklySchedule.getWeek());
        buffer.append("\n\nFor other ");
        buffer.append(name);
        buffer.append(" information, go to:\n\n    ");
        buffer.append(url);
        buffer.append("\n\nGames for ");
        buffer.append(weeklySchedule.getYear());
        buffer.append(" ");
        buffer.append(name);
        buffer.append(" ");
        buffer.append(weeklySchedule.getLabel());
        buffer.append(", due ");
        buffer.append(simpleDateFormat.format(weeklySchedule.getStartTime()));
        buffer.append(":\n");

        buffer.append("\n$\n");
        Iterator i = weeklySchedule.orderedGames().iterator(); 
        while (i.hasNext()) {
            Game game = (Game)i.next();
            buffer.append(game.getVisitor().getLongName());
            buffer.append(spacer);
            buffer.append(game.getHome().getLongName());
            buffer.append("\n");
        }

        return buffer.toString();
    }

    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append(getClass().getName());
        str.append("[");
        str.append("weeklySchedule="+weeklySchedule);
        str.append("]");

        return str.toString();
    }

    private static final SimpleDateFormat reminderDateFormat =
        new SimpleDateFormat("EEEE 'at' h:mm z");

    private static final SimpleDateFormat simpleDateFormat =
        new SimpleDateFormat("EEEE, M/d, h:mm z");

    private static final DecimalFormat decimalFormat =
        new DecimalFormat("00");

    public static void main(String argv[]) {
        com.talient.football.jdbc.JDBCHomes.setHomes();
        TextEntry te =
            new TextEntry(WeeklySchedule.getHome().findByYearWeek(2001, 2));
        try {
            System.out.print(te.formatHeader());
            System.out.print(te.formatEntry());
            System.out.print(te.formatReminderHeader());
            System.out.print(te.formatEntry());
        } catch (MissingPropertyException e) {
            System.err.println(e.toString());
        }
    }
}

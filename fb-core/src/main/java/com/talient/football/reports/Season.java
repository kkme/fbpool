// CVS ID: $Id: Season.java,v 1.2 2008-11-16 22:58:27 husker Exp $

package com.talient.football.reports;

import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;
import org.jdom.Document;
import org.jdom.Element;

import com.talient.util.Properties;
import com.talient.util.MissingPropertyException;
import com.talient.football.entities.WeeklySchedule;
import com.talient.football.entities.WeeklyResult;
import com.talient.football.entities.Entry;
import com.talient.football.entities.Entrant;
import com.talient.football.entities.Game;
import com.talient.football.reports.YearToDate;

public class Season {

    public static interface SeasonHome {

        public Season findByYear(int year);
    }

    public static SeasonHome getHome() {
        return home;
    }

    public static void setHome(SeasonHome home) {
        Season.home = home;
    }

    private static SeasonHome home = new SeasonHome() {
        public Season findByYear(int year) {
            throw new UnsupportedOperationException(
                "findByYear() not supported by default Season Home interface");
        }
    };

    private int year;

    // Total number of weeks that have a schedule
    private int totalWeeks = 0;

    // Last that has been completed
    private int lastCompleteWeek = 0;

    // Week that is currently past kick-off but all games have not been
    // completed.  Zero if there are none.
    private int weekInProgress = 0;

    // True if the season is complete
    private boolean completedSeason = false;

    // Each element is an array of winning entrants for that week
    private ArrayList weeklyWinners = new ArrayList();

    // List of season winners
    private ArrayList seasonWinners = new ArrayList();

    private ArrayList schedules = new ArrayList();

    private ArrayList dueDate = new ArrayList();

    private static final DecimalFormat df = new DecimalFormat("00");

    public Season(int year, int totalWeeks) {
        this.year = year;
        this.totalWeeks = totalWeeks;
        calculate();
    }

    public int getYear() { return year; }
    public int getTotalWeeks() { return totalWeeks; }
    public int getLastCompleteWeek() { return lastCompleteWeek; }
    public int getWeekInProgress() { return weekInProgress; }
    public boolean isSeasonComplete() { return completedSeason; }
    public List getSeasonWinners() { return seasonWinners; }
    public List getWinners(int week) {
        if (weeklyWinners.size() < week) {
            return new ArrayList();
        }
        return (List)weeklyWinners.get(week-1);
    }

    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append(getClass().getName());
        str.append("[");
        str.append("Year="+getYear());
        str.append("]");

        return str.toString();
    }

    public void calculate() {

        SimpleDateFormat dueDF = new SimpleDateFormat("EEEE, MMM. d, h:mma");

        for (int i=0; i<totalWeeks; i++) {
            Crosstable ct = Crosstable.getHome().findByYearWeek(year, i+1);
            WeeklySchedule schedule = ct.getSchedule();
            schedules.add(schedule);

            if (schedule.allGamesValidated()) {
                lastCompleteWeek = i+1;
                int topScore = 1;
                Iterator iter = ct.getResults().iterator();
                ArrayList winners = new ArrayList();
                while (iter.hasNext()) {
                    WeeklyResult wr = (WeeklyResult)iter.next();
                    if (wr.getEntrant().isGenerated()) {
                        continue;
                    }
                    if (wr.getTotalScore() >= topScore) {
                        topScore = wr.getTotalScore();
                        winners.add(wr.getEntrant());
                    }
                    else {
                        break;
                    }
                }
                weeklyWinners.add(winners);
            }
            else if (schedule.getStartTime().before(new Date())) {
                if (weekInProgress == 0) {
                    weekInProgress = i+1;
                }
            }

            if (schedule.isFinal()) {
                if (schedule.allGamesValidated()) {
                    completedSeason = true;
                }
            }

            dueDate.add(dueDF.format(schedule.getStartTime()));
        }

        YearToDate ytd = new YearToDate(year, lastCompleteWeek);
        int topScore = 1;
        Iterator iter = ytd.getStandings().iterator();
        while (iter.hasNext()) {
            Entrant entrant = (Entrant)iter.next();
            if (entrant.isGenerated()) {
                continue;
            }
            int score = ytd.getTotalPoints(entrant) -
                        ytd.getLowestWeek(entrant) -
                        ytd.getSecondLowestWeek(entrant);
            if (score >= topScore) {
                seasonWinners.add(entrant);
                topScore = score;
            }
            else {
                break;
            }
        }

        return;
    }

    public Document getJDOM()
        throws MissingPropertyException {
        final String name = Properties.getProperty("football.pool.name");
        if (name == null) {
            throw new MissingPropertyException(
                    "football.pool.name property has not been set");
        }
        final String url = Properties.getProperty("football.pool.url");
        if (url == null) {
            throw new MissingPropertyException(
                    "football.pool.url property has not been set");
        }

        Element root = new Element("Season");
        root.addContent(new Element("Title").setText(name));
        root.addContent(new Element("Year").setText(Integer.toString(year)));
        root.addContent(new Element("Url").setText(url));

        Iterator iter = seasonWinners.iterator();
        while(iter.hasNext()) {
            Entrant entrant = (Entrant)iter.next();

            Element e = null;
            if (completedSeason) {
                e = new Element("Champion");
            }
            else {
                e = new Element("Leader");
            }
            e.addContent(new Element("Week").setText(Integer.toString(lastCompleteWeek)));
            e.addContent(new Element("Name").setText(entrant.getUsername()));
            root.addContent(e);
        }

        for (int i=0; i<totalWeeks; i++) {
            Element e = new Element("Week");
            if (i < lastCompleteWeek) {
                e.setAttribute("type", "CompleteWeek");
            }
            else if (i == weekInProgress - 1) {
                e.setAttribute("type", "WeekInProgress");
            }
            else {
                e.setAttribute("type", "FutureWeek");
            }
            
            e.addContent(new Element("Label").setText(
                ((WeeklySchedule)schedules.get(i)).getLabel()));

            String yearWeek = df.format(year % 100) + df.format(i+1);
            e.addContent(new Element("WeekNum").setText(Integer.toString(i+1)));
            e.addContent(new Element("YearWeek").setText(yearWeek));
            e.addContent(new Element("Due").setText((String)dueDate.get(i)));

            if (i < weeklyWinners.size()) {
                iter = ((List)weeklyWinners.get(i)).iterator();
                while(iter.hasNext()) {
                    Entrant entrant = (Entrant)iter.next();
                    e.addContent(new Element("Winner").setText(entrant.getUsername()));
                }
            }

            root.addContent(e);
        }

        Document doc = new Document(root);
        return doc;
    }

    public String toXML() 
        throws MissingPropertyException {
        XMLOutputter outp = new XMLOutputter(Format.getPrettyFormat());
        return outp.outputString(getJDOM());
    }

    public static void main(String argv[]) {
        com.talient.football.jdbc.JDBCHomes.setHomes();
        Season season =
            new com.talient.football.jdbc.JDBCSeason().findByYear(2000);
        try {
            System.out.println(season.toXML());
        }
        catch (MissingPropertyException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
}

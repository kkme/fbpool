package com.talient.football.jdbc;

import java.io.*;
import java.util.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

import com.talient.football.entities.Entrant;
import com.talient.football.entities.Entry;
import com.talient.football.entities.WeeklySchedule;
import com.talient.football.entities.WeeklyResult;
import com.talient.football.reports.Crosstable;

public class JDBCCrosstable implements Crosstable.CrosstableHome
{
    public Crosstable findByYearWeek(int year, int week) {
        WeeklySchedule schedule = 
            WeeklySchedule.getHome().findByYearWeek(year, week);
        
        Crosstable crosstable = new Crosstable(year, week);
        crosstable.setSchedule(schedule);

        Collection entries = JDBCEntry.findByYearWeek(year, week);
        Iterator iter = entries.iterator();
        boolean setBonus = false;
        while (iter.hasNext()) {
            Entry entry = (Entry)iter.next();
            WeeklyResult result =
                JDBCWeeklyResult.findByYearWeekEntrant(year,
                                                       week,
                                                       entry.getEntrant());
            if (result == null) {
                result = new WeeklyResult(year, week, entry.getEntrant());
                result.setScore(entry.getScore());
                setBonus = true;
            }

            crosstable.add(entry, result);
        }
        if (setBonus) {
            crosstable.setBonus();
        }

        return crosstable;
    }
}

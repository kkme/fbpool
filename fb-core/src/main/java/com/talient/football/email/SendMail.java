package com.talient.football.email;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message;

import com.talient.football.entities.Entrant;
import com.talient.football.entities.Alias;
import com.talient.football.entities.Entry;
import com.talient.util.Properties;

import com.talient.football.jdbc.JDBCEntrant;

/**
 * <p>
 * @author Nick Lonsky
 * @version 1.0
 */
public class SendMail {

    private String smtp = null;
    private String to = null;
    private String from = null;
    private String cc = null;
    private String bcc = null;
    private String msg = null;
    private String subject = null;
    private Session session = null;
    private Message message = null;

    private SendMail() {};

    public void setTo(String to) throws Exception {
        this.to = to;
        if (message != null && to != null) {
            message.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
        }
    }
    public void setFrom(String from) throws Exception {
        this.from = from;
        if (message != null && from != null) {
            message.setFrom(new InternetAddress(from));
        }
    }
    public void setCC(String cc) throws Exception {
        this.cc = cc;
        if (message != null && cc != null) {
            message.setRecipient(Message.RecipientType.CC,
                    new InternetAddress(cc));
        }
    }
    public void setBCC(String bcc) throws Exception {
        this.bcc = bcc;
        if (message != null && bcc != null) {
            if (bcc.equals("")) {
                InternetAddress addresses[] = new InternetAddress[0];
                message.setRecipients(Message.RecipientType.BCC,
                        addresses);
            }
            else {
                message.setRecipient(Message.RecipientType.BCC,
                        new InternetAddress(bcc));
            }
        }
    }
    public void setSubject(String subject) throws Exception {
        this.subject = subject;
        if (message != null && subject != null) {
            message.setSubject(subject);
        }
    }
    public void setMsg(String msg) throws Exception {
        this.msg = msg;
        if (message != null && msg != null) {
            message.setText(msg);
        }
    }

    public SendMail(String subject, String msg) throws Exception {
        smtp = Properties.getProperty("football.pool.email.smtp");
        if (smtp == null) {
            throw new Exception("Missing SMTP host");
        }
        java.util.Properties props = System.getProperties();
        props.put("mail.smtp.host", smtp);
        session =  Session.getDefaultInstance(props, null);
        message = new MimeMessage(session);

        setFrom(Properties.getProperty("football.pool.email"));
        if (from == null) {
            throw new Exception("Missing From email address");
        }
        setCC(Properties.getProperty("football.pool.email.cc"));
        setBCC(Properties.getProperty("football.pool.email.bcc"));
        setSubject(subject);
        setMsg(msg);
    }

    public boolean send(Entrant entrant) {
        try {
            message.setRecipient(Message.RecipientType.TO,
                    new InternetAddress(entrant.getContactEmail()));
            Transport.send(message);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean CommnadSend(Entrant entrant) {
        try {
            String preMsg = "To: " + entrant.getContactEmail() +
                                 " (" + entrant.getUsername() + ")\n" +
                                 "From: " + from + "\n";

            String cmd = "/usr/sbin/sendmail -t -f " + from;
            Process proc = Runtime.getRuntime().exec(cmd);
            PrintWriter pw = new PrintWriter(proc.getOutputStream());
            pw.print(preMsg);
            pw.print(message);
            pw.flush();
            pw.close();
            proc.waitFor();
            if (proc.exitValue() != 0) {
                System.out.println("SendMail failed: " + cmd);
            }
        }
        catch (Exception e) {
            // System.out.println("SendMail Exception: " + e.getMessage());
            return false;
        }
        return true;
    }

    public void emailCreatedUpdated(Entrant entrant, String type)
            throws Exception {
        if (type.equals("Updated")) {
            setBCC("");
        }
        StringBuffer text = new StringBuffer();
        setSubject(Properties.getProperty("football.pool.name") + " - " +
                   type + " [" + entrant.getUsername() + "] Profile\n\n");
        text.append("The following " +
                    Properties.getProperty("football.pool.name") +
                    " Login was " + type.toLowerCase() + ":\n\n");
        text.append("    Name: " + entrant.getUsername() + "\n");
        text.append("   Email: " + entrant.getContactEmail() + "\n");
        Alias alias = JDBCEntrant.findAliasByUsername(entrant.getUsername());
        if (alias != null) {
            text.append("Password: " + alias.getPassword() + "\n\n");
        }
        if (entrant.getActive()) {
            text.append("Email Reports:\n");
            if (entrant.getWeeklyEntry()) { text.append("\tEntry\n"); }
            if (entrant.getGameOrderedRecap()) { text.append("\tRecap\n"); }
            if (entrant.getWeeklyResult()) { text.append("\tResults\n"); }
            if (entrant.getStandings()) { text.append("\tStandings\n"); }
            text.append("\nReminder Notices:\n");
            if (entrant.getNotifyLate()) { text.append("\t6 hours\n"); }
            if (entrant.getNotifyMedium()) { text.append("\t24 hours\n"); }
            if (entrant.getNotifyEarly()) { text.append("\t72 hours\n"); }
        }
        text.append("\nPlease save this email for future reference.\n\n");
        text.append(Properties.getProperty("football.pool.name") + "\n");
        text.append(Properties.getProperty("football.pool.email") + "\n");

        setMsg(text.toString());
        
        try {
            send(entrant);
        }
        catch (Exception e) {
            return;
        }
    }

    public void emailEntry(Entry entry) throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM. d, h:mm:ssa");
        StringBuffer text = new StringBuffer();
        setSubject(Properties.getProperty("football.pool.name") + " Week " +
                   entry.getSchedule().getWeek() + " Entry");
        text.append("Below is the result of an " +
                    Properties.getProperty("football.pool.name") +
                    " entry.  It was submitted by\n" +
                    entry.getEntrant().getUsername() + " on " +
                    df.format(new Date()) + " (Mountain Time)\n");
        text.append("---------------------------------------------------------------------------\n\n");
        text.append("$\n");
        for (int i=0; i<entry.size(); i++) {
            text.append(entry.get(i) + "\n");
        }
        text.append("\n---------------------------------------------------------------------------\n");

        setMsg(text.toString());
        
        try {
            send(entry.getEntrant());
        }
        catch (Exception e) {
            return;
        }
    }

    public String toString() {
        return "SMTP[" + smtp + "] " +
               "TO[" + to + "] " +
               "FROM [" + from + "] " +
               "CC[" + cc + "] " +
               "BCC[" + bcc + "] ";
    }

    public static void main(String[] args) {
        String usage =
            "Usage: com.talient.football.email.SendMail " +
            "<To> <Subject> <filename>";

        if (args.length != 3) {
            System.err.println(usage + args.length);
            System.exit(1);
        }

        File emailFile = new File(args[2]);
        String emailStr = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(emailFile));
            String line;
            while ((line = br.readLine()) != null) {
                emailStr += line + "\n";
            }
            br.close();
        }
        catch (FileNotFoundException fileE) {
            System.err.println("Could not read email file: " + emailFile +
                " Error: " + fileE.getMessage());
            System.exit(1);
        }
        catch (IOException ioE) {
            System.err.println("Could not read email file: " + emailFile +
                " Error: " + ioE.getMessage());
            System.exit(1);
        }

        Entrant entrant = JDBCEntrant.findByUsername(args[0]);
        if (entrant == null) {
            System.err.println("Could not find entrant " + args[0]);
            System.exit(1);
        }

        try {
            SendMail email = new SendMail(args[1], emailStr);
            if (! email.send(entrant)) {
                System.err.println("Could not send email");
            }
        }
        catch (Exception e) {
            System.err.println("Could not send email: " + e.getMessage());
        }
    }
}

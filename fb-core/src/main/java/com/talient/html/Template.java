package com.talient.html;

import java.io.*;
import java.util.*;

public class Template
{
    private String tplt = "";
    private String resourceName = "";
    private boolean valid = false;
    private boolean cgi = true;

    public Template(String resource) 
    {
        this.resourceName = resource;
        try
        {
            reset();
        }
        catch (Exception e) {
            System.err.println("Template::Exception::" + e.toString());
        }
    }

    public String  getTemplate()                { return tplt; }
    public String  getResourceName()            { return resourceName; }
    public void    setResourceName(String name) { resourceName = name; }
    public boolean isValid()                    { return valid; }
    public boolean isCgi()                      { return cgi; }
    public void    setCgi(boolean flag)         { cgi = flag; }

    public void reset()
    {
        try {
            InputStream is = getClass().getResourceAsStream(resourceName);
            if (is == null) {
                System.err.println("Missing template resource: " +
                                    resourceName);
                is = getClass().getResourceAsStream(
                                "/com/talient/html/MissingResource.htm");
                if (is == null) {
                    System.err.println("Could not find MissingResource.htm");
                }
                valid = false;
            }
            else {
                valid = true;
            }

            BufferedReader input =
                new BufferedReader(new InputStreamReader(is));

            tplt = "";
            String line;
            while ((line = input.readLine()) != null) {
                tplt += line + "\n";
            }
            input.close();

            if (!isValid()) {
                replace("#text", resourceName);
            }
        }
        catch (IOException ioE) {
            System.out.println(ioE.getMessage());
            ioE.printStackTrace();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


    public void replace(String oldStr, String newStr) 
    {
        if (newStr == null) {
             newStr = "";
        }

        int start = 0;
        while (start >= 0) {
            int index = tplt.indexOf(oldStr, start);
            if (index >= 0) {
                String s1 = tplt.substring(0, index);
                String s2 = tplt.substring(index + oldStr.length(),
                                           tplt.length());
                tplt = s1 + newStr + s2;
                start = index + newStr.length();
            }
            else {
                start = -1;
            }
        }
        return;
    }

    public void print(PrintWriter pw) 
    {
        if (isCgi()) {
            pw.print("Content-type: text/html\n\n");
        }
        pw.print(tplt);
    }


    public static void main(String[] args) {
        Template tplt = new Template("/com/talient/html/BadTpltDir.htm");
        System.out.println(tplt.getTemplate());
    }
}
